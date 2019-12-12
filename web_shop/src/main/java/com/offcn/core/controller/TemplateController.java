package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.template.TypeTemplate;
import com.offcn.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {
    @Reference
    private TemplateService templateService;

    //修改模板要先查到
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }

    //根据模板id  查询规格的集合 和规格选项集合
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        List<Map> bySpecList = templateService.findBySpecList(id);
        return bySpecList;
    }


   /* //模块分页
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody TypeTemplate typeTemplate,int page,int rows){
        return templateService.findPage(typeTemplate,page,rows);
    }*/
    //添加模块
   /* @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try{
            templateService.add(typeTemplate);
            return new Result(true,"添成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }*/

    /*//修改模板
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try{
            templateService.update(typeTemplate);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }*/
   /* //删除模板
    @RequestMapping("/delete")
    public Result delete(Long ids []){
        try{
            templateService.delete(ids);
            return new Result(true,"删除成功");
        }catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }*/
}
