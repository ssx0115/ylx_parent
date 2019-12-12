package com.offcn.core.service;

import java.util.Map;

public interface SearchService {
    //使用solr搜索商品
    public Map<String,Object> search(Map paramMap);
}
