package com.doodeec.silversparrow.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "orders")
public class Order implements DBEntity {

    @DatabaseField(generatedId = true) long id;
    @DatabaseField String contactId;
    @Expose @SerializedName("name") @DatabaseField String name;
    @Expose @SerializedName("count") @DatabaseField int count;

    //no-op constructor needed for ormlite
    public Order() {
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getContactId() {
        return contactId;
    }

    @Override public String toString() {
        return "Order[" +
                "id=" + id +
                ",name=" + name +
                ",count=" + count +
                ",contactId=" + contactId +
                "]";
    }
}
