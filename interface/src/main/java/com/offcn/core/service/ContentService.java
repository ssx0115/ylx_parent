package com.offcn.core.service;

import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface ContentService {
    //查询所有广告信息
    public List<Content> list();
    //广告信息分页
    //public PageResult getPage(int pageNum, int pageSize);
    //增加广告信息
    public void insert(Content content);
    //修改广告信息
    public Content findOne(Long id);
    public void update(Content content);
    //广告信息分页
    public PageResult search(Content content, int pageNum, int pageSize);
    //删除广告信息
    public void delete(Long ids[]);

    //前台界面
    //查询轮播图显示广告
    public List<Content> findByCategoryId(Long categoryId);
    //从redis数据库中查询广告显示到轮播图中
    public List<Content> findByCategoryIdFromRedis(Long categoryId);

}
