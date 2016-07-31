package com.doodeec.silversparrow.network.response;

import com.doodeec.silversparrow.data.model.Order;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class OrdersResponse extends BaseResponse {

    @Expose @SerializedName("items") List<Order> orderList;

    public List<Order> getOrders() {
        return orderList;
    }
}
