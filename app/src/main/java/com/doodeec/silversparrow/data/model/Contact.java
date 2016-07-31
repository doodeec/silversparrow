package com.doodeec.silversparrow.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "contacts")
public class Contact implements DBEntity {

    @Expose @SerializedName("id") @DatabaseField(id = true) String id;
    @Expose @SerializedName("name") @DatabaseField String name;
    @Expose @SerializedName("phone") @DatabaseField String phone;
    @Expose @SerializedName("pictureUrl") @DatabaseField String image;

    //no-op constructor needed for ormlite
    public Contact() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override public String toString() {
        return "Contact[" +
                "id=" + id +
                ",name=" + name +
                ",phone=" + phone +
                ",image=" + image +
                "]";
    }
}
