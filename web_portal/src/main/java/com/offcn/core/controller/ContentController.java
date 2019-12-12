package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.pojo.ad.Content;
import com.offcn.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    //显示轮播图广告信息
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryIdFromRedis(Long categoryId){
        return contentService.findByCategoryIdFromRedis(categoryId);
    }
}
