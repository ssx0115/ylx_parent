package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private SearchService searchService;
    //使用solr搜索商品
    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map paramMap){
        Map<String, Object> resultMap = searchService.search(paramMap);
        return resultMap;
    }
}
