package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.core.mapper.good.GoodsDescMapper;
import com.offcn.core.mapper.good.GoodsMapper;
import com.offcn.core.mapper.item.ItemCatMapper;
import com.offcn.core.mapper.item.ItemMapper;
import com.offcn.core.pojo.good.Goods;
import com.offcn.core.pojo.good.GoodsDesc;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.item.ItemCat;
import com.offcn.core.pojo.item.ItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {
    @Autowired
    private GoodsMapper goodsMapper;//商品mapper
    @Autowired
    private GoodsDescMapper goodsDescMapper;//商品详情的mapper
    @Autowired
    private ItemMapper itemMapper;//库存的mapper
    @Autowired
    private ItemCatMapper itemCatMapper;//商品分类的mapper
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    //从mysql数据库中取数据并封装起来
    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        //查询商品的列表信息
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        //查询商品的详情信息
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        //查询库存的列表集合
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemMapper.selectByExample(example);
        //查询对应商品的分类信息
        if(goods != null){
            ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
            //将商品的分类分类信息封装到map集合中
            resultMap.put("itemCat1",itemCat1.getName());
            resultMap.put("itemCat2",itemCat2.getName());
            resultMap.put("itemCat3",itemCat3);
        }
        //将商品列表信息，商品详情，库存放入到结果map集合中
        resultMap.put("goods",goods);
        resultMap.put("goodsDesc",goodsDesc);
        resultMap.put("itemList",itemList);
        return resultMap;
    }

    //根据生成的数据生成对应的静态页面
    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //获取模板的初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //创建生成模板的位置和名称
        String path = goodsId + ".html";
        //得到绝对路径
        String realPath = getRealPath(path);
        //使用输出流将模板对象写到对应的位置
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //生成
        template.process(rootMap,out);
        //关闭流
        out.close();
    }

    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        System.out.println(realPath);
        return realPath;
    }

    @Override
    public void setServletContext(javax.servlet.ServletContext servletContext) {

    }
}
