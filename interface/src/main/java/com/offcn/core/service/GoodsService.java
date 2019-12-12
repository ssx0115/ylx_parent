package com.offcn.core.service;

import com.offcn.core.bean.GoodsEntity;
import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.good.Goods;

public interface GoodsService {
    //条件查询商品审核信息并分页
    public PageResult search(Goods goods,int pageNum,int pageSize);
    //查看商品审核信息的详情
    public GoodsEntity findOne(Long id);
    //删除商品审核信息
    public void delete(Long id);
    //修改商品信息的状态
    public void updateStatus(Long id,String status);
    //商家后台的商品的添加
    public void add(GoodsEntity goodsEntity);
    //修改商品信息
    public void update(GoodsEntity goodsEntity);
    //上下架商品就是修改tb_goods表的is_marketable字段。1表示上架、0表示下架。
    public void updateMarketable(Long ids[],String marketable);
}
