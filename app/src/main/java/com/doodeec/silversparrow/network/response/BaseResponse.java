package com.doodeec.silversparrow.network.response;

import android.support.annotation.Nullable;

import com.doodeec.silversparrow.data.model.ResponseError;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Dusan Bartos
 */
public abstract class BaseResponse {

    @Expose @SerializedName("error") ResponseError error;

    @Nullable public ResponseError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
