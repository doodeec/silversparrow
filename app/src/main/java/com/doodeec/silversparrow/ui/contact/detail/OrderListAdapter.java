package com.doodeec.silversparrow.ui.contact.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.data.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Products list adapter
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private List<Order> orderList = new ArrayList<>();

    @Override public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_order, parent, false));
    }

    @Override public void onBindViewHolder(OrderViewHolder holder, int position) {
        final Order order = orderList.get(position);
        holder.setName(order.getName());
        holder.setCount(order.getCount());
    }

    public void updateData(List<Order> orders) {
        orderList = orders;
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return orderList.size();
    }
}