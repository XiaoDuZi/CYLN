package com.guanghua.ln.bean;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/6/23 0023.
 */

public class RecordIDBean {

    /**
     * recordId : 415749
     * code : 500
     * msg : 系统异常
     */

    private int recordId;
    private String code;
    private String msg;

    public static RecordIDBean objectFromData(String str) {

        return new Gson().fromJson(str, RecordIDBean.class);
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RecordIDBean{" +
                "recordId=" + recordId +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
