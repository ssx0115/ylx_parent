package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.item.ItemCatMapper;
import com.offcn.core.pojo.item.ItemCat;
import com.offcn.core.pojo.item.ItemCatExample;
import com.offcn.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //根据父级的id查询每一级商品分类信息
    @Override
    public List<ItemCat> findList(Long parentId) {
        //先将商品分类的所有信息查出来
        List<ItemCat> itemCatList = itemCatMapper.selectByExample(null);
        //分类名称作为key，模板的id作为value值
        for (ItemCat itemCat : itemCatList) {
            //将商品分类信息放入到redis中
            redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
        }

        ItemCatExample example = new ItemCatExample();
        ItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatMapper.selectByExample(example);
    }
    //修改商品分类要先查到
    @Override
    public ItemCat findOne(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }
    //修改商品分类
    @Override
    public void update(ItemCat itemCat) {
        itemCatMapper.updateByPrimaryKeySelective(itemCat);
    }

    //添加商品分类
    @Override
    public void add(ItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }
    //删除商品分类信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            itemCatMapper.deleteByPrimaryKey(id);
        }
    }

    //查询所有商品分类信息
    @Override
    public List<ItemCat> findAll() {
        List<ItemCat> itemCatList = itemCatMapper.selectByExample(null);
        return itemCatList;
    }

}
