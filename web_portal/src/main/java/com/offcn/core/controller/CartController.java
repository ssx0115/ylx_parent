package com.offcn.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.core.bean.Cart;
import com.offcn.core.bean.Result;
import com.offcn.core.service.CartService;
import com.offcn.core.util.Constants;
import com.offcn.core.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    //添加商品到购物车
   @RequestMapping("/addGoodsToCartList")
   @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")//相当于设置响应头的信息
   public Result addGoodsToCartList(Long itemId,Integer num){//库存id和数量
       try {
           //1.获取当前登录的用户名称
           String userName = SecurityContextHolder.getContext().getAuthentication().getName();
           //2.获取购物车列表
           List<Cart> cartList = findCartList();
           //3.将当前的商品加入到购物车列表中
           cartList = cartService.addGoodsToCartList(cartList, itemId, num);
           //4.判断用户是否登录，
           if("anonymouseUser".equals(userName)) {
               //如何未登录将购物车列表存入到cookie中
               CookieUtil.setCookie(request, response, Constants.CART_LIST_COOKIE, JSON.toJSONString(cartList),3600*24*30,"UTF-8");
           }else{
               //如果登录将购物车列表存入到redis中
               cartService.setCartList(userName,cartList);
           }
           return new Result(true, "添加成功");
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false, "添加失败");
       }
   }
    //获取购物车返回的数据
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
       //先获取当前登录的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie中获取列表
        String cartListString = CookieUtil.getCookieValue(request, Constants.CART_LIST_COOKIE,"UTF-8");
        //如果购物车列表json串为空
        if(cartListString == null || "".equals(cartListString)){
            cartListString="[]";
        }
        //将购物车列表转为json
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        //判断用户是否登录
        if("anonymouseUser".equals(userName)) {
            return cartList_cookie;
        }else{
            //已经登录从cookie中获取购物车数据
            List<Cart> cartListFromRedis = cartService.getCartListFromRedis(userName);
            if(cartList_cookie.size() > 0){
                //redis 中和cookie中购物车列表要合并成一个购物车对象
                cartService.getCookieCartListFromRedis(cartList_cookie,cartListFromRedis);
                //删除cookie中的购物车列表
                CookieUtil.deleteCookie(request,response,Constants.CART_LIST_COOKIE);
                //将合并后的购物车列表存到redis中
                cartService.setCartList(userName,cartListFromRedis);
            }
            //返回购物车列表对象
            return cartListFromRedis;
        }
    }

}
