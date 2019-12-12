package com.offcn.core.service;

import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.good.Brand;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询所有品牌信息
    public List<Map> list();
    //品牌信息分页
    public PageResult getPage(int pageNum,int pageSize);
    //增加品牌信息
    public void insert(Brand brand);
    //修改品牌信息
    public Brand findOne(Long id);
    public void update(Brand brand);
    //条件查询品牌信息
    public PageResult getPage(Brand brand,int pageNum,int pageSize);
    //删除品牌信息
    public void delete(Long ids[]);

}
