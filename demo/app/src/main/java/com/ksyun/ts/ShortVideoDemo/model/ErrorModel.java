package com.ksyun.ts.ShortVideoDemo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class ErrorModel {
    @SerializedName("Type")
    private String type;
    @SerializedName("Code")
    private String code;
    @SerializedName("Message")
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
