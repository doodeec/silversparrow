package com.doodeec.silversparrow.ui.contact.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Dusan Bartos
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contact_list_item_name) TextView nameView;
    @BindView(R.id.contact_list_item_phone) TextView phoneView;
    @BindView(R.id.contact_list_item_image) ImageView image;

    public ContactViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setPhone(String phone) {
        phoneView.setText(phone);
    }

    public void setImage(String imageUrl) {
        Util.releaseImage(image);
        Util.setImage(image, imageUrl, R.drawable.placeholder);
    }
}
