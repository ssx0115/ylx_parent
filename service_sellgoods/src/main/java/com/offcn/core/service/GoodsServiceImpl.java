package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.GoodsEntity;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.good.BrandMapper;
import com.offcn.core.mapper.good.GoodsDescMapper;
import com.offcn.core.mapper.good.GoodsMapper;
import com.offcn.core.mapper.item.ItemCatMapper;
import com.offcn.core.mapper.item.ItemMapper;
import com.offcn.core.mapper.seller.SellerMapper;
import com.offcn.core.pojo.good.Brand;
import com.offcn.core.pojo.good.Goods;
import com.offcn.core.pojo.good.GoodsDesc;
import com.offcn.core.pojo.good.GoodsExample;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.item.ItemCat;
import com.offcn.core.pojo.item.ItemExample;
import com.offcn.core.pojo.seller.Seller;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;//商品的mapper
    @Autowired
    private GoodsDescMapper goodsDescMapper;//商品详情的mapper
    @Autowired
    private ItemMapper itemMapper;//库存的mapper
    @Autowired
    private ItemCatMapper itemCatMapper;//商品分类的mapper
    @Autowired
    private BrandMapper brandMapper;//商品品牌的mapper
    @Autowired
    private SellerMapper sellerMapper;//商家管理的mapper
    @Autowired
    private JmsTemplate jmsTemplate;
    //商品上架使用
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;
    //为商品的下架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;
    //条件查询商品信息并分页
    @Override
    public PageResult search(Goods goods, int pageNum, int pageSize) {
        //先使用分页插件分页
        PageHelper.startPage(pageNum,pageSize);
        //查询所有商品信息
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        if(goods != null){
            if(goods.getAuditStatus() != null && goods.getAuditStatus().length() != 0){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if(goods.getGoodsName() != null && goods.getGoodsName().length() != 0){
                criteria.andGoodsNameLike("%"+ goods.getGoodsName() +"%");
            }
        }
        Page<Goods> page = (Page<Goods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //查询商品审核信息的详情
    @Override
    public GoodsEntity findOne(Long id) {
        GoodsEntity goodsEntity = new GoodsEntity();
        //查询商品的基本信息商品介绍
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        //查询商品SKU列表
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemMapper.selectByExample(example);
        goodsEntity.setItemList(items);

        //查询商品的详细信息
        goodsEntity.setGoods(goods);
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goodsEntity.setGoodsDesc(goodsDesc);
        return goodsEntity;
    }

    //删除商品审核信息
    @Override
    public void delete(Long id) {

        Goods goods = goodsMapper.selectByPrimaryKey(id);
        goods.setIsDelete("1");
        goodsMapper.updateByPrimaryKeySelective(goods);

    }

    //修改商品信息的状态
    @Override
    public void updateStatus(Long id,String status) {
        //通过id修改商品的状态
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        goods.setAuditStatus(status);
        goods.setIsMarketable("1");
        goodsMapper.updateByPrimaryKeySelective(goods);

        //通过id修改库存的状态
        Item item = new Item();
        item.setStatus(status);
        ItemExample example = new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        itemMapper.updateByExampleSelective(item,example);
        //将商品的id作为消息发送给消息服务器
        jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;
            }
        });
    }

    //商家后台的商品的添加
    @Override
    public void add(GoodsEntity goodsEntity) {
        //保存商品对象
        goodsEntity.getGoods().setAuditStatus("0");
        goodsMapper.insertSelective(goodsEntity.getGoods());
        //2 保存商品详情对象
        //商品的主键作为商品详情的主键  记得更改Mapper   resource文件下 GoodsMapper.xml
        //<insert id="insertSelective" parameterType="cn.lijun.core.pojo.good.Goods" useGeneratedKeys="true" keyProperty="id">
        //insert into tb_goods
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        goodsDescMapper.insertSelective(goodsEntity.getGoodsDesc());//插入商品扩展数据
        //3 保存库存集合对象
        insertItm(goodsEntity);
    }
    public void insertItm(GoodsEntity goodsEntity){
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            // 勾选复选框  有库存数据
            if(goodsEntity.getItemList()!=null){
                // 库存对象
                for(Item item:goodsEntity.getItemList()){
                    // 标题由商品名+规格组成 供消费者搜索使用
                    String title = goodsEntity.getGoods().getGoodsName();
                    String specJsonStr = item.getSpec();
                    // 将json  转成对象
                    Map speMap = JSON.parseObject(specJsonStr, Map.class);
                    // 获取speMap中的value集合
                    Collection<String> values = speMap.values();
                    for(String value:values){
                        // title=title+value   小米手机 5g版本 64g 电信版
                        title+=" "+value;
                    }
                    item.setTitle(title);
                    //  设置库存的对象的属性值
                    setItemValue(goodsEntity,item);
                    itemMapper.insertSelective(item);
                }
            }
        }else {
            //  没有勾选   没有库存  但是初始化一条
            Item item = new Item();
            item.setPrice(new BigDecimal("666666666666"));
            // 库存量
            item.setNum(0);
            // 初始化规格
            item.setSpec("{}");
            //标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            // 设置库存对象的属性值
            setItemValue(goodsEntity,item);
            itemMapper.insertSelective(item);

        }
    }
    private Item setItemValue(GoodsEntity goodsEntity,Item item){
        // 商品的id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建时间
        item.setCreateTime(new Date());
        // 更新的时间
        item.setUpdateTime(new Date());
        // 库存的状态
        item.setStatus("0");
        // 分类的id  库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        // 分类的名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        // 品牌的名称
        Brand brand = brandMapper.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        // 卖家名称
        Seller seller = sellerMapper.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //卖家的id
        item.setSellerId(seller.getSellerId());
        // 式例的图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if(maps!=null&&maps.size()>0){
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }


    //修改商品的信息
    @Override
    public void update(GoodsEntity goodsEntity) {
        goodsEntity.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
        goodsMapper.updateByPrimaryKey(goodsEntity.getGoods());//保存商品表
        goodsDescMapper.updateByPrimaryKey(goodsEntity.getGoodsDesc());//保存商品扩展表
        //删除原有的sku列表数据
        ItemExample example=new ItemExample();
        ItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        insertItm(goodsEntity);//插入商品SKU列表数据
    }

    //上下架商品就是修改tb_goods表的is_marketable字段。1表示上架、0表示下架。
    @Override
    public void updateMarketable(Long ids[],String marketable) {
        for (Long id : ids) {
            Goods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsMarketable(marketable);
            goodsMapper.updateByPrimaryKeySelective(goods);
            if("0".equals(marketable)){
                //将商品的id作为消息发送给消息服务器
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
            if("1".equals(marketable)){
                //将商品的id作为消息发送给消息服务器
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }


        }
    }

}
