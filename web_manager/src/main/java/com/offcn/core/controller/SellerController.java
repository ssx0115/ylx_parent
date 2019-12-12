package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.seller.Seller;
import com.offcn.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    //分页查询显示所有商家入驻信息
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller,int page,int rows){
        return sellerService.search(seller,page,rows);
    }
    //查询商家入驻的详情
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }
    //修改商家入驻信息的状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try{
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
}
