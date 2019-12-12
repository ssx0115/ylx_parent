package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.core.mapper.address.AddressMapper;
import com.offcn.core.pojo.address.Address;
import com.offcn.core.pojo.address.AddressExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;
    //
    @Override
    public List<Address> findListByLoginUser(String userName) {
        AddressExample example = new AddressExample();
        AddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userName);
        return addressMapper.selectByExample(example);
    }
}
