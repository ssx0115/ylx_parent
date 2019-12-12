package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.GoodsEntity;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.good.Goods;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.service.GoodsService;
import com.offcn.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//商家后台的商品管理
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    /*@Reference
    private SolrManagerService solrManagerService;*/

    //添加商品信息
    @RequestMapping("add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        try {
            //卖家名称使用当前登录的用户名称
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(name);

            goodsService.add(goodsEntity);
            return new Result(true,"添加商品成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加商品失败");
        }
    }

    //商品修改先回显到goods_edit.html页面
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }

    //条件查询商品审核信息并分页
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,int page,int rows){
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //添加查询条件
        goods.setSellerId(sellerId);
        return goodsService.search(goods, page, rows);
    }


    //删除商品审核信息
    @RequestMapping("/delete")
    public Result delete(Long ids []){
        try{
            if(ids != null && ids.length > 0){
                for (Long id : ids) {
                    goodsService.delete(id);
                    //solrManagerService.deleteItemSolr(id);
                }
            }
            return new Result(true,"删除成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //修改商品信息
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity){
        try{
            //获取当前的登录名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            //如果传递过来的商家ID并不是当前登录的用户的ID,则属于非法操作
            if(!goodsEntity.getGoods().getSellerId().equals(sellerId) ||  !goodsEntity.getGoods().getSellerId().equals(sellerId) ){
                return new Result(false, "操作非法");
            }
            goodsService.update(goodsEntity);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //上下架商品就是修改tb_goods表的is_marketable字段。1表示上架、0表示下架。
    @RequestMapping("/updateMarketable")
    public Result updateMarketable(Long ids[],String marketable){
        try{
            goodsService.updateMarketable(ids,marketable);
            return new Result(true,"上下架成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"上下架失败");
        }
    }

}
