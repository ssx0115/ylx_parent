package com.offcn.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.core.bean.Cart;
import com.offcn.core.mapper.item.ItemMapper;
import com.offcn.core.pojo.item.Item;
import com.offcn.core.pojo.order.OrderItem;
import com.offcn.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1 根据商品ID 查询商品的信息
        Item item = itemMapper.selectByPrimaryKey(itemId);
        //2 判断商品是否存在  抛异常
        if(item == null){
            throw new RuntimeException("此商品不存在");
        }
        //3判断该商品是否为1   已经审核状态  状态不对抛异常
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("此商品未通过审核 不允许买卖");
        }
        //4 获取商家的id
        String sellerId = item.getSellerId();
        // 5 根据商家的ID 查询购物车列表中是否存在该商家的购物车
        Cart cart = findCartBySellerId(cartList, sellerId);
        //6 判断如果该购物车不存在该商家的购物车
        if (cart == null){
            //6.1  新建购物车对象
            cart = new Cart();
            //  6.2  创建购物车对象卖家id
            cart.setSellerId(sellerId);
            //  6.3  创建购物车对象卖家名称
            cart.setSellerName(item.getSeller());
            //  6.4  创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<OrderItem>();
            //  6.5  创建购物车选项集合
            OrderItem orderItem = createOrderItem(item, num);
            //  6.6  将购物项加入到购物车选项集合中
            orderItemList.add(orderItem);
            //  6.7  将购物车选项集合加进购物车中
            cart.setOrderItemList(orderItemList);
            //  6.8  将新建的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else {
            //   否则如果购物车列表中存在着商家的购物车
            List<OrderItem> orderItemList = cart.getOrderItemList();
            OrderItem oneItem= findOneItemId(orderItemList, itemId);
            //  6.  9  判断购物车明细是否为空
            if(oneItem == null){
                //  6.10为空  则添加新的明细
                oneItem = createOrderItem(item,num);
                orderItemList.add(oneItem);
            }else {
                //  6. 11不为空 在原来购物车的基础上   添加商品的数量 更改金额
                oneItem.setNum(oneItem.getNum() + num);
                //  6.  12  设置总价
                oneItem.setTotalFee(item.getPrice().multiply(new BigDecimal(oneItem.getNum())));
                //  6.  13 如果购物车明细数量《=0  则删除
                if(oneItem.getNum() <= 0){
                    orderItemList.remove(oneItem);
                }
                //  6.  9  如果购物车明细表数量为空   则移除
                if(orderItemList.size() <= 0){
                    cartList.remove(cart);
                }
            }
        }
        //7 返回购物车列表对象
        return cartList;
    }

    @Override
    public void setCartList(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).put(userName,cartList);
    }

    @Override
    public List<Cart> getCartListFromRedis(String userName) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userName);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    //从cookie中获取购物车数据
    @Override
    public List<Cart> getCookieCartListFromRedis(List<Cart> cookieCartList, List<Cart> redisCartList) {
        if(cookieCartList != null){
            for (Cart cart : cookieCartList) {
                for (OrderItem orderItem : cart.getOrderItemList()) {
                    //将购物车集合加入到redis购物车集合中
                    redisCartList = addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
                }
            }
        }
        return redisCartList;
    }

    // 5 根据商家的ID 查询购物车列表中是否有卖家对象
    private Cart findCartBySellerId(List<Cart> cartList,String sellerId){
        if(cartList != null){
            for (Cart cart : cartList) {
                if(cart.getSellerId().equals(sellerId)){
                    return cart;
                }
            }
        }
        return null;
    }
    //  6.5  创建购物车选项集合
    private OrderItem createOrderItem(Item item,Integer num){
        if(num < 0){
            throw new RuntimeException("购买数量非法");
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setNum(num);//购买的数量
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());//卖家的id
        orderItem.setTitle(item.getTitle());
        //计算总价
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
    //从购物车中查询是否存在这个商品
    private OrderItem findOneItemId(List<OrderItem> orderItemList,Long itemId){
        if(orderItemList != null){
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)){
                    return orderItem;
                }
            }
        }
        return null;
    }



}

