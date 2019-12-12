package com.offcn.core.service;

import com.offcn.core.bean.Cart;

import java.util.List;

public interface CartService {
    //添加商品到当前登录用户的购物车中
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );
    public void setCartList(String userName,List<Cart> cartList);
    public List<Cart> getCartListFromRedis(String userName);
    public List<Cart> getCookieCartListFromRedis(List<Cart> cookieCartList,List<Cart> redisCartList);
}
