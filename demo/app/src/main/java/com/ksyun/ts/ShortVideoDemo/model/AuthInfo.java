package com.ksyun.ts.ShortVideoDemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by xiaoqiang on 2017/11/23.
 */

public class AuthInfo {
    @SerializedName("PackageName")
    private String packageName;
    @SerializedName("UserRefer")
    private String userRefer;
    @SerializedName("Interval")
    private long interval;
    @SerializedName("Timeout")
    private long timerOut;
    @SerializedName("Modules")
    private String modules;
    @SerializedName("Message")
    private String message;
    @SerializedName("LastModified")
    private long lastModiafied;
    @SerializedName("ModuleList")
    private List<Map<String, String>> moduleList;
    @Expose
    private long time; // 本地时间。获取到鉴权的时间

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUserRefer() {
        return userRefer;
    }

    public void setUserRefer(String userRefer) {
        this.userRefer = userRefer;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getTimerOut() {
        return timerOut;
    }

    public void setTimerOut(long timerOut) {
        this.timerOut = timerOut;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getLastModiafied() {
        return lastModiafied;
    }

    public void setLastModiafied(long lastModiafied) {
        this.lastModiafied = lastModiafied;
    }

    public List<Map<String, String>> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Map<String, String>> moduleList) {
        this.moduleList = moduleList;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AuthInfo{" +
                "packageName='" + packageName + '\'' +
                ", userRefer='" + userRefer + '\'' +
                ", interval=" + interval +
                ", timerOut=" + timerOut +
                ", modules='" + modules + '\'' +
                ", message='" + message + '\'' +
                ", lastModiafied=" + lastModiafied +
                ", moduleList=" + moduleList +
                ", time=" + time +
                '}';
    }
}
