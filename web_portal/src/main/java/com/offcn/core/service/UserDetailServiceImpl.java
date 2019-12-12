package com.offcn.core.service;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/*自定义认证类，在此之前负责用户的密码校验工作
* 现在cas和SpringSecurity集成，集成后用户名和密码交给cas管理*/
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建权限集合
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        //向权限集合中加入访问权限
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",list);
    }
}
