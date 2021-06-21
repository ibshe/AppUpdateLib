package com.boge.update.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @Author ibshen@aliyun.com
 */
public class VersionModel implements Serializable {

    /**
     * {
     *   "versionCode":5,
     *   "versionName":"1.0.5",
     *   "content":"1、新增xx报表及导出#2、修复已知bug",
     *   "minSupport":4,
     *   "url":"http://110.88.192.240:8088/app/water.apk",
     *   "updateTitle":发现新版本,
     *   "mustUpdate":false,
     *   "date":"2021-05-07 09:10:15"
     * }
     */
    private int versionCode;
    private String versionName;
    private String content;
    private int minSupport;
    private String url;
    private String updateTitle;
    private boolean mustUpdate;
    private String date;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(int minSupport) {
        this.minSupport = minSupport;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public boolean isMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    @Override
    public String toString() {
        return "VersionModel{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", content='" + content + '\'' +
                ", minSupport=" + minSupport +
                ", url='" + url + '\'' +
                ", updateTitle='" + updateTitle + '\'' +
                ", mustUpdate=" + mustUpdate +
                ", date='" + date + '\'' +
                '}';
    }

    public VersionModel parse(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        versionCode = object.optInt("versionCode");
        versionName = object.optString("versionName","");
        content = object.optString("content","");
        url = object.optString("url","");
        minSupport = object.optInt("minSupport",0);
        updateTitle = object.optString("updateTitle","");
        mustUpdate = object.optBoolean("mustUpdate",false);
        date = object.optString("date","");
        return this;
    }
}
