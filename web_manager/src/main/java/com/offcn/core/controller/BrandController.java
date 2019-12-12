package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.good.Brand;
import com.offcn.core.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    //获取品牌所有信息
    @RequestMapping("/list")
    public List<Map> findAllBrand(){
        return brandService.list();
    }
    //分页操作
    @RequestMapping("/findPage")
    public PageResult getPage(Integer page,Integer rows){
        PageResult pageResult = brandService.getPage(page, rows);
        return pageResult;
    }
    //增加品牌信息
    @RequestMapping("/insert")
    public Result insert(@RequestBody Brand brand){
        try{
            brandService.insert(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //修改品牌信息要先查到
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }
    //修改品牌信息
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }
    //按条件查询品牌信息
    @RequestMapping("/find")
    public PageResult find(@RequestBody Brand brand,int page,int rows){
        return brandService.getPage(brand, page, rows);
    }
    //删除品牌信息
    @RequestMapping("/delete")
    public Result delete(Long ids[]){
        try{
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

}
