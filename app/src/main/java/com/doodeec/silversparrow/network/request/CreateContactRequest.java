package com.doodeec.silversparrow.network.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Dusan Bartos
 */
public class CreateContactRequest {

    @Expose @SerializedName("name") protected String name;
    @Expose @SerializedName("phone") protected String phone;

    public CreateContactRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
