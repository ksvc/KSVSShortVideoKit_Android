package com.ksyun.ts.ShortVideoDemo.model;

import java.io.Serializable;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class User implements Serializable {
    private String nickname;
    private String uid;
    private Boolean gender;
    private String token;
    private String headUrl;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
