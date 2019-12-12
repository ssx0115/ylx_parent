package com.offcn.core.mapper.address;

import com.offcn.core.pojo.address.Provinces;
import com.offcn.core.pojo.address.ProvincesExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProvincesMapper {
    int countByExample(ProvincesExample example);

    int deleteByExample(ProvincesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Provinces record);

    int insertSelective(Provinces record);

    List<Provinces> selectByExample(ProvincesExample example);

    Provinces selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Provinces record, @Param("example") ProvincesExample example);

    int updateByExample(@Param("record") Provinces record, @Param("example") ProvincesExample example);

    int updateByPrimaryKeySelective(Provinces record);

    int updateByPrimaryKey(Provinces record);
}