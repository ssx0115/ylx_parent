package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.good.BrandMapper;
import com.offcn.core.pojo.good.Brand;
import com.offcn.core.pojo.good.BrandExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    //查询所有品牌信息
    @Override
    public List<Map> list() {
        return brandMapper.list();
    }

    //品牌信息分页
    @Override
    public PageResult getPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Brand> brands = (Page<Brand>) brandMapper.selectByExample(null);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    //增加品牌信息
    @Override
    public void insert(Brand brand) {
        brandMapper.insert(brand);
    }

    //修改品牌信息先查询
    @Override
    public Brand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    //修改品牌信息
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    //按条件查询品牌信息
    @Override
    public PageResult getPage(Brand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        BrandExample example = new BrandExample();
        BrandExample.Criteria criteria = example.createCriteria();
        if(brand != null){
            if(brand.getName() != null && brand.getName().length() != 0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length() != 0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }


        Page<Brand> brands = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    //删除品牌信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }
}
