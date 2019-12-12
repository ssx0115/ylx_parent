package com.offcn.core.service;

public interface SolrManagerService {
    //添加数据到solr中
    public void insertItemToSolr(Long id);
    //删除solr中的数据
    public void deleteItemSolr(Long id);
}
