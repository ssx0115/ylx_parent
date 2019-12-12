package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.ad.ContentCategoryMapper;
import com.offcn.core.mapper.ad.ContentMapper;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.pojo.ad.ContentCategory;
import com.offcn.core.pojo.ad.ContentCategoryExample;
import com.offcn.core.pojo.ad.ContentExample;
import com.offcn.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //查询所有广告信息
    @Override
    public List<Content> list() {
        return contentMapper.selectByExample(null);
    }
    //广告信息分页
    @Override
    public PageResult search(Content content,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Content> contents = (Page<Content>) contentMapper.selectByExample(null);
        return new PageResult(contents.getTotal(),contents.getResult());
    }
    //增加广告信息
    @Override
    public void insert(Content content) {
        //将广告信息添加到数据库中
        contentMapper.insertSelective(content);
        //将Redis中的广告信息缓存删除
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }
    //修改广告信息
    @Override
    public Content findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }
    //修改广告信息
    @Override
    public void update(Content content) {
        //1 先从数据库中查询到原来的广告信息
        Content oldContent = contentMapper.selectByPrimaryKey(content.getId());
        //2 根据原来广告对象中的分类id 到redis数据库删除对应的广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //3 根据传入的最新广告分类对象中的id  删除redis中对应的广告数据集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        // 4 将新的广告对象更新到mysql 数据库
        contentMapper.updateByPrimaryKeySelective(content);


    }
    //删除广告信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //先查询到要删除的广告信息，
            Content content = contentMapper.selectByPrimaryKey(id);
            //删除redis中的该广告信息
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
            contentMapper.deleteByPrimaryKey(id);
        }
    }

    //查询轮播图显示广告
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        return contentMapper.selectByExample(example);
    }

    //从redis数据库中查询广告，显示在轮播图中
    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //先查询redis数据库看里面有没有广告的数据
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        if(contentList == null){
            contentList =  findByCategoryId(categoryId);
            //将广告信息添加到redis数据库中
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,contentList);
        }
        return contentList;
    }
}
