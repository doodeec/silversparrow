package com.doodeec.silversparrow.network.response;

import com.doodeec.silversparrow.data.model.Contact;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class ContactsResponse extends BaseResponse {

    @Expose @SerializedName("items") List<Contact> contactList;

    public List<Contact> getContacts() {
        return contactList;
    }
}
