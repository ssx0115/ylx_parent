package com.offcn.core.mapper.seckill;

import com.offcn.core.pojo.seckill.SeckillGoods;
import com.offcn.core.pojo.seckill.SeckillGoodsExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeckillGoodsMapper {
    int countByExample(SeckillGoodsExample example);

    int deleteByExample(SeckillGoodsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SeckillGoods record);

    int insertSelective(SeckillGoods record);

    List<SeckillGoods> selectByExample(SeckillGoodsExample example);

    SeckillGoods selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SeckillGoods record, @Param("example") SeckillGoodsExample example);

    int updateByExample(@Param("record") SeckillGoods record, @Param("example") SeckillGoodsExample example);

    int updateByPrimaryKeySelective(SeckillGoods record);

    int updateByPrimaryKey(SeckillGoods record);
}