package com.doodeec.silversparrow.ui.contact.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.data.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Products list adapter
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<Contact> contactList = new ArrayList<>();

    @Override public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact, parent, false));
    }

    @Override public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.setName(contact.getName());
        holder.setPhone(contact.getPhone());
        holder.setImage(contact.getImage());
    }

    /**
     * Notifies list about changes
     *
     * @param contacts new data
     */
    public void updateData(List<Contact> contacts) {
        contactList = contacts;
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return contactList.size();
    }

    @Nullable public Contact getItem(int position) {
        if (position < 0 || position >= contactList.size()) return null;
        return contactList.get(position);
    }
}