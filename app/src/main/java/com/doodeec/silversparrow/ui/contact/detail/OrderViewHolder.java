package com.doodeec.silversparrow.ui.contact.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doodeec.silversparrow.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Dusan Bartos
 */
public class OrderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.order_list_item_name) TextView nameView;
    @BindView(R.id.order_list_item_count) TextView countView;

    public OrderViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setCount(int count) {
        countView.setText(String.format(Locale.ENGLISH, "%dx", count));
    }
}
