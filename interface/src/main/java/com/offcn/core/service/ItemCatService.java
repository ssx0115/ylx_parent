package com.offcn.core.service;
import com.offcn.core.bean.PageResult;
import com.offcn.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    //查询每一级商品分类
    public List<ItemCat> findList(Long parentId);
    //修改商品要先查询到
    public ItemCat findOne(Long id);
    //修改商品分类
    public void update(ItemCat itemCat);
    //添加上商品分类
    public void add(ItemCat itemCat);
    //删除商品分类信息
    public void delete(Long ids []);
    //查询所有商品分类信息
    public List<ItemCat> findAll();

}
