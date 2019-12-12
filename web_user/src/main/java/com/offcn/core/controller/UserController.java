package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.bean.Result;
import com.offcn.core.pojo.user.User;
import com.offcn.core.service.UserService;
import com.offcn.core.util.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    //发送短信验证码
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try{
            if(phone == null && "".equals(phone)){
                return new Result(false,"手机号码不能为空");
            }
            if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false,"手机号码格式不正确");
            }
            userService.sendCode(phone);
            return new Result(true,"短信验证码发送成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"短信验证码发送失败");
        }
    }
    //注册用户并核对短信验证码是否输入正确
    @RequestMapping("/add")
    public Result add(@RequestBody User user,String smscode){
        try{
            //获取user对象中的手机号
            String phone = user.getPhone();
            Boolean isSuccess = userService.checkCode(phone, smscode);
            if(!isSuccess){
                return new Result(false,"手机验证码输入不正确请重新输入");
            }
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setSourceType("Y");
            user.setStatus("1");
            userService.add(user);
            return new Result(true,"注册成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
}
