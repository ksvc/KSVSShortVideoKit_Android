package com.ksyun.ts.ShortVideoDemo.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by xiaoqiang on 2017/11/6.
 */

public class BodyBuild {
    private JSONObject mJson;

    public BodyBuild() {
        mJson = new JSONObject();
    }

    public BodyBuild bindDeviceID(String deviceID) {
        try {
            mJson.put("deviceId", deviceID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindMobile(String mobild) {
        try {
            mJson.put("mobile", mobild);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindCode(String code) {
        try {
            mJson.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindBucket(String bucket) {
        try {
            mJson.put("bucket", bucket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindHeadUrlPath(String headUrlPath) {
        try {
            mJson.put("headUrlPath", headUrlPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindNickname(String nickname) {
        try {
            mJson.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BodyBuild bindGender(boolean gender) {
        try {
            mJson.put("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public RequestBody build() {
        if (mJson == null || mJson.length() <= 0) {
            return null;
        }
        return RequestBody.create(MediaType.parse("application/json"), mJson.toString());
    }
}
