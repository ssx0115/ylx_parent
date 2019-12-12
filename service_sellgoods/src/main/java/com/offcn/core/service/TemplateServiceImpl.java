package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.core.bean.PageResult;
import com.offcn.core.mapper.specification.SpecificationOptionMapper;
import com.offcn.core.mapper.template.TypeTemplateMapper;
import com.offcn.core.pojo.specification.SpecificationOption;
import com.offcn.core.pojo.specification.SpecificationOptionExample;
import com.offcn.core.pojo.template.TypeTemplate;
import com.offcn.core.pojo.template.TypeTemplateExample;
import com.offcn.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;//规格选项的mapper
    @Autowired
    private RedisTemplate redisTemplate;

    //模板模块分页
    @Override
    public PageResult findPage(TypeTemplate typeTemplate,int pageNum, int pageSize) {
        //redis中缓存模板的所有数据
        List<TypeTemplate> typeTemplateList = typeTemplateMapper.selectByExample(null);
        //模板的id作为键，品牌的集合作为value存入redis
        for (TypeTemplate template : typeTemplateList) {
            //获得品牌集合
            String brandIds = template.getBrandIds();

            //将json转换成集合存进去
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);
            //获得规格集合
            List<Map> specList = findBySpecList(template.getId());
            //存到redis中
            redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).put(template.getId(),brandList);
            redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).put(template.getId(),specList);
        }
        //模板分页
        PageHelper.startPage(pageNum,pageSize);
        TypeTemplateExample example = new TypeTemplateExample();
        TypeTemplateExample.Criteria criteria = example.createCriteria();
        if(typeTemplate != null){
            if(typeTemplate.getName() != null && typeTemplate.getName().length() != 0){
                criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
        }
        Page<TypeTemplate> pages = (Page<TypeTemplate>) typeTemplateMapper.selectByExample(example);
        return new PageResult(pages.getTotal(),pages.getResult());
    }

    //添加模块
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }

    //修改模块要先查到模块
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }
    //修改模块
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }
    //删除模板
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }
    //模块下拉列表数据
    @Override
    public List<Map> selectOptionList() {
        List<Map> maps = typeTemplateMapper.selectOptionList();
        return maps;
    }
    //根据模板id  查询规格的集合 和规格选项集合
    @Override
    public List<Map> findBySpecList(Long id) {
        //1 根据模板id  查询模板对象
        TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        //2 从模板对象中 获取规格的数据   获取的是json 字符串
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //3 将json 转List集合对象
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //4 遍历集合对象
        if(maps!=null){
            for(Map map:maps){
                // 5 遍历 根据规格id  查询对应的规格选项数据
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                //6  将规格选项  再封装道到规格选项中 一起返回
                SpecificationOptionExample example = new SpecificationOptionExample();
                SpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                // 根据规格id  获得规格选项数据
                List<SpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
                // 将规格选项集合封装到原来的map 中
                map.put("options",optionList);

            }

        }
        return maps;
    }
}
