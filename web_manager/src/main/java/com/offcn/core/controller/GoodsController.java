package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.GoodsEntity;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.good.Goods;
import com.offcn.core.service.CmsService;
import com.offcn.core.service.GoodsService;
import com.offcn.core.service.SolrManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    /*@Reference
    private CmsService cmsService;
    @Reference
    private SolrManagerService solrManagerService;*/
    //条件查询商品审核信息并分页
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,int page,int rows){
        return goodsService.search(goods, page, rows);
    }
    //查询商品审核信息的详情
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
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

    //修改商品信息的状态
    @RequestMapping("/updateStatus")
    public Result updatePass(Long ids [],String status){
        try{
            if(ids != null && ids.length > 0){
                for (Long id : ids) {
                    goodsService.updateStatus(id,status);
                    /*if("1".equals(status)){
                        solrManagerService.insertItemToSolr(id);
                        //取数据
                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
                        //创建模板页面
                        cmsService.createStaticPage(id,goodsData);
                    }*/
                }
            }
            return new Result(true,"审核通过");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"您的审核不通过");
        }
    }
}
