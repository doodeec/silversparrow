package com.doodeec.silversparrow.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Dusan Bartos
 */
public class ResponseError {

    @Expose @SerializedName("message") String message;
    @Expose @SerializedName("code") int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
