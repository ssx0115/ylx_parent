package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.offcn.core.mapper.user.UserMapper;
import com.offcn.core.pojo.user.User;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue smsDestination;
    @Value("${template_code}")
    private String template_code;//模板的编号
    @Value("${sign_name}")
    private String sign_name;//签名
    @Autowired
    private UserMapper userMapper;

    //发送短信验证码
    @Override
    public void sendCode(String phone) {
        //随机生成六位随机数
        StringBuffer buffer = new StringBuffer();
        for (int i = 1;i < 7;i ++){
            int a = new Random().nextInt(10);
            buffer.append(a);
        }
        //2 将手机号码为键  验证码为值 存到redis中  生存时间10分钟
        redisTemplate.boundValueOps(phone).set(buffer.toString(),60*10, TimeUnit.SECONDS);
        final String smsCode =  buffer.toString();
        // 3 将手机号码  短信内容 模板编号  签名  封装到map 消息发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //获得消息对象
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",phone);
                mapMessage.setString("template_code",template_code);
                mapMessage.setString("sign_name",sign_name);
                //将短信内容封装到map集合中
                Map map = new HashMap();
                map.put("code",smsCode);
                mapMessage.setString("param", JSON.toJSONString(map));
                return (Message)mapMessage;
            }
        });
    }

    //核对短信验证码是否输入正确
    @Override
    public Boolean checkCode(String phone,String smscode) {
        //判断输入的手机号和验证码是否为空
        if(smscode == null || phone == null || "".equals(phone) || "".equals(smscode)){
            return false;
        }
        //先从redis中通过手机号码找到存进去的验证码
        String code = (String) redisTemplate.boundValueOps(phone).get();
        //判读验证码是否输入正确
        if(smscode.equals(code)){
            return true;
        }
        return false;
    }

    //注册用户
    @Override
    public void add(User user) {
        userMapper.insertSelective(user);
    }
}
