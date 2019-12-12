package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    //查询所有所有广告信息
    @RequestMapping("/findAll")
    public List<Content> findAllBrand(){
        return contentService.list();
    }
    //分页操作
    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows){
        PageResult pageResult = contentService.search(content,page, rows);
        return pageResult;
    }
    //增加品牌信息
    @RequestMapping("/add")
    public Result insert(@RequestBody Content content){
        try{
            contentService.insert(content);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //修改品牌信息要先查到
    @RequestMapping("/findOne")
    public Content findOne(Long id){
        return contentService.findOne(id);
    }
    //修改品牌信息
    @RequestMapping("/update")
    public Result update(@RequestBody Content content){
        try{
            contentService.update(content);
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
            contentService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
