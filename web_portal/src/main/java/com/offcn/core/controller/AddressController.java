package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.core.pojo.address.Address;
import com.offcn.core.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference
    private AddressService addressService;

    @RequestMapping("/findListByLoginUser")
    public List<Address> findListByLoginUser(){
        //通过安全框架获取当前的登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByLoginUser(userName);
    }
}
