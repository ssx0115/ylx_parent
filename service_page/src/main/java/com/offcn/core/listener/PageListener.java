package com.offcn.core.listener;

import com.offcn.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

public class PageListener implements MessageListener {
    @Autowired
    private CmsService cmsService;
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQMessage = (ActiveMQTextMessage)message;
        try {
            //获取id
            String id = activeMQMessage.getText();
            //从mysql中取数据
            Map<String, Object> goodsData = cmsService.findGoodsData(Long.parseLong(id));
            //根据生成的数据生成静态页面
            cmsService.createStaticPage(Long.parseLong(id),goodsData);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
