package com.offcn.core.service;

import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.SpecEntity;
import com.offcn.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    //获取规格的全部信息
    public List<Map> list();
    //条件查询并分页
    public PageResult findPage(Specification specification,int pageNum,int pageSize);
    //添加规格
    public void add(SpecEntity specEntity);
    //修改规格要先查到规格信息
    public SpecEntity findOne(Long id);
    //修改规格信息
    public void update(SpecEntity specEntity);
    //删除规格信息
    public void delete(Long ids[]);
}
