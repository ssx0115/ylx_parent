package com.offcn.core.service;

import java.util.Map;

public interface CmsService {
    //取数据
    public Map<String,Object> findGoodsData(Long goodsId);
    //根据取到的数据 生成页面
    public void createStaticPage(Long goodsId, Map<String,Object> rootMap) throws Exception;
}
