package com.offcn.core.service;

import com.offcn.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    //查询当前登录用户的地址
    public List<Address> findListByLoginUser(String userName);
}
