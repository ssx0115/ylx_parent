package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.bean.SpecEntity;
import com.offcn.core.mapper.specification.SpecificationMapper;
import com.offcn.core.mapper.specification.SpecificationOptionMapper;
import com.offcn.core.pojo.specification.Specification;
import com.offcn.core.pojo.specification.SpecificationExample;
import com.offcn.core.pojo.specification.SpecificationOption;
import com.offcn.core.pojo.specification.SpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    //获取全部规格信息
    @Override
    public List<Map> list() {
        return specificationMapper.list();
    }

    //条件查询并分页
    @Override
    public PageResult findPage(Specification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        SpecificationExample example = new SpecificationExample();
        SpecificationExample.Criteria criteria = example.createCriteria();
        if(specification != null){
            if(specification.getSpecName() != null && specification.getSpecName().length() != 0){
                criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }
        }
        Page<Specification> page = (Page<Specification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }
    //添加规格
    @Override
    public void add(SpecEntity specEntity) {
        //先添加规格
        specificationMapper.insertSelective(specEntity.getSpecification());
        if(specEntity.getSpecificationOptionList() != null){
            List<SpecificationOption> specificationOptionList = specEntity.getSpecificationOptionList();
            for (SpecificationOption option : specificationOptionList) {
                option.setSpecId(specEntity.getSpecification().getId());
                //添加规格选项
                specificationOptionMapper.insertSelective(option);
            }
        }
    }
    //修改规格要先查到
    @Override
    public SpecEntity findOne(Long id) {
        //先查询到规格对象
        Specification specification = specificationMapper.selectByPrimaryKey(id);

        Long id1 = specification.getId();
        //查询到规格选项对象
        SpecificationOptionExample example = new SpecificationOptionExample();
        SpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id1);
        List<SpecificationOption> optionList = specificationOptionMapper.selectByExample(example);

        SpecEntity specEntity = new SpecEntity();
        specEntity.setSpecificationOptionList(optionList);
        specEntity.setSpecification(specification);
        return specEntity;
    }
    //修改规格
    @Override
    public void update(SpecEntity specEntity) {
        //先修改规格信息
        Specification specification = specEntity.getSpecification();
        specificationMapper.updateByPrimaryKey(specification);
        //获取修改的规格信息的id
        Long id = specification.getId();
        //先删除对应规格选项的信息
        if(specEntity.getSpecificationOptionList() != null){
            SpecificationOptionExample example = new SpecificationOptionExample();
            SpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
        //重新添加规格选项的信息
        for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
            option.setSpecId(id);
            specificationOptionMapper.insert(option);
        }
    }
    //删除规格信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格信息
            specificationMapper.deleteByPrimaryKey(id);
            //删除对应的规格选项信息
            SpecificationOptionExample example = new SpecificationOptionExample();
            SpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }


}
