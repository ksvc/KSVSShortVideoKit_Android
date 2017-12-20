package com.ksyun.ts.ShortVideoDemo.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ksyun.ts.ShortVideoDemo.model.User;

/**
 * Created by xiaoqiang on 2017/11/28.
 */

public class UserLocalization {
    private SharedPreferences mSharedPreferences;

    public UserLocalization(Context context) {
        mSharedPreferences = context.getSharedPreferences("user_tab",
                Context.MODE_PRIVATE);
    }

    public void saveData(User data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nickname", data.getNickname());
        editor.putString("uid", data.getUid());
        editor.putBoolean("gender", data.getGender());
        editor.putString("token", data.getToken());
        editor.putString("headUrl", data.getHeadUrl());
        editor.apply();
    }


    public User getData() {
        User user = new User();
        user.setNickname(mSharedPreferences.getString("nickname", "大西瓜"));
        user.setUid(mSharedPreferences.getString("uid", null));
        user.setGender((mSharedPreferences.getBoolean("gender", false)));
        user.setToken((mSharedPreferences.getString("token", null)));
        user.setHeadUrl((mSharedPreferences.getString("headUrl", "")));
        if (TextUtils.isEmpty(user.getUid()) || TextUtils.isEmpty(user.getToken())) {
            return null;
        } else {
            return user;
        }
    }

    public void clearAuthData() {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove("nickname");
            editor.remove("uid");
            editor.remove("gender");
            editor.remove("token");
            editor.remove("headUrl");
            editor.apply();
        }
    }
}
