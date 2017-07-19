package com.guanghua.ln.bean;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class AuthenticationBean {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "AuthenticationBean{" +
                "result='" + result + '\'' +
                '}';
    }
}
