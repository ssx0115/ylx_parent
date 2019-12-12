package com.offcn.core.listener;


import com.offcn.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;


public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;
        try{
            String goodsId = atm.getText();
            solrManagerService.insertItemToSolr(Long.parseLong(goodsId));
        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
