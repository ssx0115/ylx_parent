package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    //使用solr搜索商品
    @Override
    public Map<String, Object> search(Map paramMap) {
        //1 先根据传过来的关键字查询对应的分类信息的总数据量，总页数，当前页码
        Map<String, Object> resultMap = highlightSearch(paramMap);
        //2 (得到的是分类集合)根据查询的参数到solr中获取对应的分类结果，  因为分类有重复要按分组的方式去重复
        List<String> groupCateGroupList = findGroupCateGroupList(paramMap);
        //将分类的集合放入到查询到集合中
        resultMap.put("categoryList",groupCateGroupList);
        //3 判断paramMap传入的参数中是否有分类的名称  查询品牌和规格列表
        String category = String.valueOf(paramMap.get("category"));
        if(category != null && !"".equals(category)){
            //5 如果有分类参数  则根据分类查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(category);
            resultMap.putAll(specListAndBrandList);
        }else{
            //4 如果没有 根据第一个分类查询对应的商品集合
            Map specListAndBrandList = findSpecListAndBrandList(groupCateGroupList.get(0));
            resultMap.putAll(specListAndBrandList);
        }

        return resultMap;
    }

    //1.高亮显示
    private Map<String,Object> highlightSearch(Map paramMap){
        //获取前端传过来的查询条件
        String keywords = String.valueOf(paramMap.get("keywords"));
        //去除空格等关键字
        if(keywords != null){
            keywords = keywords.replaceAll(" ","");
        }

        //获取当前的页码
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        //获取当前页的条数
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
        //获得页面传过来的分类条件
        String category = String.valueOf(paramMap.get("category"));
        //获取页面中的品牌的过滤条件
        String brand = String.valueOf(paramMap.get("brand"));
        //获取页面中的规格的过滤条件
        String spec = String.valueOf(paramMap.get("spec"));
        //获取页面中的价格的过滤条件
        String price = String.valueOf(paramMap.get("price"));
        //获取页面传入的排序的域对象
        String sortField = String.valueOf(paramMap.get("sortField"));
        //获取页面传入的排序的方式
        String sort = String.valueOf(paramMap.get("sort"));


        //创建一个对象
        HighlightQuery query = new SimpleHighlightQuery();

        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //判断当前的页码
        if(pageNo == null || pageNo <= 0){
            pageNo = 1;
        }
        //设置每页开始的记录数
        Integer start = (pageNo-1)*pageSize;
        //设置从第几页开始查
        query.setOffset(start);
        //设置每页查询的条数
        query.setRows(pageSize);

        //按分类进行过滤筛选
        if(category != null && !"".equals(category)){
            Criteria criteria1 = new Criteria("item_category").is(paramMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            //将查询条件放入到对象中
            query.addFilterQuery(filterQuery);
        }
        //按品牌进行过滤筛选
        if(brand != null && !"".equals(brand)){
            Criteria criteria2 = new Criteria("item_brand").is(paramMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria2);
            //将查询条件放入到对象中
            query.addFilterQuery(filterQuery);
        }
        //根据规格进行过滤
        if(spec != null && !"".equals(spec)){
            Map<String,String> specMap = (Map) paramMap.get("spec");
            for (String key : specMap.keySet()) {
                //创建查询条件
                Criteria criteria3 = new Criteria("item_spec_"+key).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria3);
                //将查询条件放入到对象中
                query.addFilterQuery(filterQuery);
            }
        }
        //根据价格来进行查询  添加的根据价格来进行过滤的操作
        if(price != null && !"".equals(price)){
            Criteria criteria4 = null;
            String[] split = price.split("-");
            //判断价格的区间,创建查询条件
            if("*".equals(split[1])){
                criteria4 = new Criteria("item_price").greaterThanEqual(Double.parseDouble(split[0]));
            }else {
                criteria4 = new Criteria("item_price").between(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            }
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria4);
            //将价格的查询条件放入到查询对象中
            query.addFilterQuery(filterQuery);
        }
        //添加排序条件
        if(sort != null && sortField != null && !"".equals(sort) && !"".equals(sortField)){
            //升序排序
            if("ASC".equals(sort)){
                //创建排序对象
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                //将排序条件放到查询对象中
                query.addSort(orders);
            }
            //降序排序
            if("DESC".equals(sort)){
                //创建排序对象
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
                //将排序条件放到查询对象中
                query.addSort(orders);
            }
        }

        //创建高亮显示对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置需要高亮显示的域
        highlightOptions.addField("item_title");
        //高亮的前缀
        highlightOptions.setSimplePrefix("<em style=\"color:green\">");
        //高亮的后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮对象加入到查询对象中
        query.setHighlightOptions(highlightOptions);
        //从solr中查询并返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query,Item.class);
        //获取带高亮的集合
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if(highlights != null && highlights.size() > 0){
                //获取高亮的标题集合
                List<String> highlightTitle = highlights.get(0).getSnipplets();
                if(highlightTitle != null && highlightTitle.size() > 0){
                    //获取高亮的标题
                    String title = highlightTitle.get(0);
                    item.setTitle(title);
                }
            }
            itemList.add(item);
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("rows",items.getContent());//所有的内容信息
        resultMap.put("total",items.getTotalElements());//总记录条数
        resultMap.put("totalPages",items.getTotalPages());//总页数
        return resultMap;
    }
    //2 根据查询的参数到solr中获取对应的分类结果，  因为分类有重复要按分组的方式去重复
    private List<String> findGroupCateGroupList(Map paramMap){
        List<String> resultList = new ArrayList<>();
        //获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        //去除空格等关键字
        if(keywords != null){
            keywords = keywords.replaceAll(" ","");
        }

        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询的条件放入到查询对象中
        query.addCriteria(criteria);

        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        //将分组对象放入到查询对象中
        query.setGroupOptions(groupOptions);
        //使用分组查询  获得分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获得结果集合 分类域对象
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分类域中的实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历实体集合得到实体对象
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            //放到集合中
            resultList.add(groupValue);
        }
        return resultList;
    }

    //4 根据分类名称查询对应品牌集合和规格集合
    private Map findSpecListAndBrandList(String categoryName){
        //根据分类名称获得对应的分类的id
        Long id = (Long)redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        //根据分类的id获得对应的品牌集合
        List brandList  = (List) redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(id);
        //根据分类的id获得对应的规格集合
        List specList = (List)redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(id);
        //将品牌和规格集合放入到集合中
        Map resultMap = new HashMap();
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;
    }


}
