package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.ad.ContentCategoryMapper;
import com.offcn.core.pojo.ad.ContentCategory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    //查询所有广告分类信息
    @Override
    public List<ContentCategory> list() {
        return contentCategoryMapper.selectByExample(null);
    }
    //广告分类信息分页
    @Override
    public PageResult search(ContentCategory contentCategory,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<ContentCategory> contents = (Page<ContentCategory>) contentCategoryMapper.selectByExample(null);
        return new PageResult(contents.getTotal(),contents.getResult());
    }
    //增加广告分类信息
    @Override
    public void insert(ContentCategory contentCategory) {
        contentCategoryMapper.insert(contentCategory);
    }
    //修改广告分类信息
    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryMapper.selectByPrimaryKey(id);
    }
    //修改广告分类信息
    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
    }
    //删除广告分类信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            contentCategoryMapper.deleteByPrimaryKey(id);
        }
    }
}
