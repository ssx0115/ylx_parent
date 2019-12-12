package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.bean.SpecEntity;
import com.offcn.core.pojo.specification.Specification;
import com.offcn.core.service.SpecificationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //获取规格所有信息
    @RequestMapping("/list")
    public List<Map> list(){
        return specificationService.list();
    }

    //规格的条件查询并分页
    @RequestMapping("/search")
    public PageResult find(@RequestBody Specification specification,int page,int rows){
        return specificationService.findPage(specification,page,rows);
    }
    //添加规格信息
    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity){
        try{
            specificationService.add(specEntity);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //修改规格信息要先查到
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        SpecEntity one = specificationService.findOne(id);
        return one;
    }
    //修改规格信息
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity){
        try{
            specificationService.update(specEntity);
            return new Result(true,"更新成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }
    //删除规格信息
    @RequestMapping("/delete")
    public Result delete(Long ids []){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
