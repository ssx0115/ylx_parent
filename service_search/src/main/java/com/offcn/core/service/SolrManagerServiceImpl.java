package com.offcn.core.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.offcn.core.mapper.item.ItemMapper;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.item.ItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;

@Service
public class SolrManagerServiceImpl implements SolrManagerService{
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    //添加数据到solr中
    @Override
    public void insertItemToSolr(Long id) {
        //要先从数据库中查询到对应的库存对象
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemMapper.selectByExample(example);
        //遍历库存对象
        if(itemList != null){
            for (Item item : itemList) {
                String spec = item.getSpec();
                //将库存对象转换成对应的Map集合
                Map specMap = JSON.parseObject(spec, Map.class);
                //将库存的map集合放入库存的查询条件中
                item.setSpecMap(specMap);
            }
            //将得到的库存数据放入到查询条件中
            solrTemplate.saveBeans(itemList);
            //提交
            solrTemplate.commit();
        }
    }

    //删除solr中对应的数据
    @Override
    public void deleteItemSolr(Long id) {
        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_goodsid").is(id);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();

    }
}
