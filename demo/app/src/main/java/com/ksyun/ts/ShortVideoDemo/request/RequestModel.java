package com.ksyun.ts.ShortVideoDemo.request;

import com.google.gson.annotations.SerializedName;
import com.ksyun.ts.ShortVideoDemo.model.ErrorModel;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class RequestModel<T> {
    private String result;
    private String message;
    @SerializedName("RequestId")
    private String requestId;
    @SerializedName("Error")
    private ErrorModel error;
    private T data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
    }
}
