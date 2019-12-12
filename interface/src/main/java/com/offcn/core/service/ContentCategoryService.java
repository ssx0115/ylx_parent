package com.offcn.core.service;

import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.pojo.ad.ContentCategory;

import java.util.List;

public interface ContentCategoryService {
    //查询所有广告分类信息
    public List<ContentCategory> list();
    //广告信息分类分页
    //public PageResult getPage(int pageNum, int pageSize);
    //增加广告分类信息
    public void insert(ContentCategory contentCategory);
    //修改广告分类信息
    public ContentCategory findOne(Long id);
    public void update(ContentCategory contentCategory);
    //广告信息分类分页
    public PageResult search(ContentCategory contentCategory, int pageNum, int pageSize);
    //删除广告分类信息
    public void delete(Long ids[]);

}
