package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.pojo.ad.ContentCategory;
import com.offcn.core.service.ContentCategoryService;
import com.offcn.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference
    private ContentCategoryService contentCategoryService;
    //查询所有所有广告信息
    @RequestMapping("/findAll")
    public List<ContentCategory> findAllBrand(){
        return contentCategoryService.list();
    }
    //分页操作
    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory, Integer page, Integer rows){
        PageResult pageResult = contentCategoryService.search(contentCategory,page, rows);
        return pageResult;
    }
    //增加品牌信息
    @RequestMapping("/add")
    public Result insert(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.insert(contentCategory);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //修改品牌信息要先查到
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return contentCategoryService.findOne(id);
    }
    //修改品牌信息
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.update(contentCategory);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }
    //删除品牌信息
    @RequestMapping("/delete")
    public Result delete(Long ids[]){
        try{
            contentCategoryService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
