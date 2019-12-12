package com.offcn.core.service;

import com.github.pagehelper.Page;
import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    //模板分页
    public PageResult findPage(TypeTemplate typeTemplate,int pageNum,int pageSize);
    //添加模板
    public void add(TypeTemplate typeTemplate);
    //修改模板要先查询到
    public TypeTemplate findOne(Long id);
    //修改模板
    public void update(TypeTemplate typeTemplate);
    //删除模板
    public void delete(Long ids []);
    //查询对应的类型列表
    public List<Map> selectOptionList();
    //根据模板id  查询规格的集合 和规格选项集合
    public List<Map> findBySpecList(Long id);
}
