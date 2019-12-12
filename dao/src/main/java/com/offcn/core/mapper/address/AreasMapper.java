package com.offcn.core.mapper.address;

import com.offcn.core.pojo.address.Areas;
import com.offcn.core.pojo.address.AreasExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AreasMapper {
    int countByExample(AreasExample example);

    int deleteByExample(AreasExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Areas record);

    int insertSelective(Areas record);

    List<Areas> selectByExample(AreasExample example);

    Areas selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Areas record, @Param("example") AreasExample example);

    int updateByExample(@Param("record") Areas record, @Param("example") AreasExample example);

    int updateByPrimaryKeySelective(Areas record);

    int updateByPrimaryKey(Areas record);
}