package com.offcn.core.service;

import com.offcn.core.pojo.user.User;

public interface UserService {
    //发送短信验证码
    public void sendCode(String phone);
    //核对验证码是否正确
    public Boolean checkCode(String phone,String smscode);
    //注册用户
    public void add(User user);
}
