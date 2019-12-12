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

    //添加商家入驻信息
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        try{
            sellerService.add(seller);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //分页查询显示所有商家入驻信息
    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller,int page,int rows){
        return sellerService.search(seller,page,rows);
    }

}
