package com.offcn.core.service;

import com.offcn.core.pojo.seller.Seller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/*自定义验证类 实现UserDetailsService */
public class UserDetailServiceImpl implements UserDetailsService {
    private SellerService sellerService;
    public void setSellerService(SellerService sellerService){
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*登录验证用户名是否为空，如果为空直接返回null*/
        /*根据用户名到数据库查询   用户对象*/
        /*如果用户查不到  返回null*/
        /*如果用户对象查询到  判断用户是否审核通过，  如果未通过   返回null*/
        /*如果成功返回user对象， 将用户名和密码   返回权限集合*/
        /*框架帮助比对用户名和密码*/
        // 权限集合
        List<GrantedAuthority> authList = new ArrayList<>();
        // 具体具有什么的权限
        authList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //1   判断用户名是否为null    如果为null   直接返回null
        if(username==null){
            return null;
        }
        //2   根据用户名到数据库查询   用户对象
        Seller seller = sellerService.findOne(username);
        //3 如果用户查不到   返回null
        if(seller!=null){
            //4   如果用户对象查到了 判断用户审核 是否通过  如果未通过返回null
            if("1".equals(seller.getStatus())){
                //5    返回user 对象 将用户名 密码 返回权限集合
                return new User(username,seller.getPassword(),authList);
            }
        }
        return null;
        //6 框架帮助比对用户名和密码是否匹配
    }
}
