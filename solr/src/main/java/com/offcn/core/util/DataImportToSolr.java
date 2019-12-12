package com.offcn.core.util;

import com.alibaba.fastjson.JSON;
import com.offcn.core.mapper.item.ItemMapper;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.item.ItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportToSolr {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;

    //向solr中加入数据
    private void insertData() {
        //查出item库存表中的所有数据
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> items = itemMapper.selectByExample(example);
        if (items != null) {
            //获得spec数据转换成map集合
            for (Item item : items) {
                String spec = item.getSpec();
                Map map = JSON.parseObject(String.valueOf(spec), Map.class);
                item.setSpecMap(map);
            }
            //将库存数据加入到solr中
            solrTemplate.saveBeans(items);
            //提交
            solrTemplate.commit();
        }
    }

    //删除solr中的数据
    private void delete(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        /*solrTemplate.deleteById("1369318");*/
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr dataImportToSolr = (DataImportToSolr) context.getBean("dataImportToSolr");
        dataImportToSolr.delete();
    }
}
