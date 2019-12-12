package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.item.ItemCat;
import com.offcn.core.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference
    private ItemCatService itemCatService;
    //查询分类信息
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findList(parentId);
    }
    //修改分类信息要先查到
    @RequestMapping("findOne")
    public ItemCat findOone(Long id){
        return itemCatService.findOne(id);
    }
    //修改分类信息
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.update(itemCat);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    //添加商品分类信息
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        try{
            itemCatService.add(itemCat);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //删除商品分类信息
    @RequestMapping("delete")
    public Result delete(Long ids[]){
        try{
            itemCatService.delete(ids);
            return new Result(true,"删除成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    //查询所有分类信息
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

}
