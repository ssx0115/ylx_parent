package com.offcn.core.bean;

import com.offcn.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private String SellerId;//商家的id
    private String SellerName;//商家名称
    private List<OrderItem> orderItemList;//购物车明细

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
