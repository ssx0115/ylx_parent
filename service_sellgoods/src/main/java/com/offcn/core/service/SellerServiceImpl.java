package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.seller.SellerMapper;
import com.offcn.core.pojo.seller.Seller;
import com.offcn.core.pojo.seller.SellerExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;
    //添加商家入驻信息
    @Override
    public void add(Seller seller) {
        seller.setCreateTime(new Date());
        seller.setStatus("0");
        sellerMapper.insert(seller);
    }
    //条件查询并分页
    @Override
    public PageResult search(Seller seller, int pageNum, int pageSize) {
        //使用分页助手先分页
        PageHelper.startPage(pageNum,pageSize);
        SellerExample example = new SellerExample();
        SellerExample.Criteria criteria = example.createCriteria();

        if (!"-1".equals(seller.getStatus())){
            criteria.andStatusEqualTo(seller.getStatus());
        }

        if(seller.getName() != null && seller.getName().length() != 0){
            criteria.andNameLike("%"+ seller.getName() +"%");
        }
        if(seller.getNickName() != null && seller.getNickName().length() != 0){
            criteria.andNickNameLike("%"+ seller.getNickName() +"%");
        }


        Page<Seller> sellers = (Page<Seller>) sellerMapper.selectByExample(example);
        return new PageResult(sellers.getTotal(),sellers.getResult());
    }
    //查询商家入驻信息的详情信息
    @Override
    public Seller findOne(String sellerId) {
        return sellerMapper.selectByPrimaryKey(sellerId);
    }
    //修改商家入驻信息的状态
    @Override
    public void updateStatus(String sellerId,String status) {
        SellerExample example = new SellerExample();
        SellerExample.Criteria criteria = example.createCriteria();
        criteria.andSellerIdEqualTo(sellerId);
        Seller seller = sellerMapper.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerMapper.updateByExampleSelective(seller,example);
    }
}
