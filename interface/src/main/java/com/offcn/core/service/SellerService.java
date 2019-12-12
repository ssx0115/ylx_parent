package com.offcn.core.service;

import com.github.pagehelper.Page;
import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.seller.Seller;

import java.util.List;

public interface SellerService {

    //添加商家入驻信息
    public void add(Seller seller);
    //条件查询并分页
    public PageResult search(Seller seller, int pageNum, int pageSize);
    //查询商家入驻信息的详情
    public Seller findOne(String sellerId);
    //修改商家入驻的状态
    public void updateStatus(String sellerId,String status);
}
