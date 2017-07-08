package com.guanghua.ln.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class SmallVideoItemBean {

    /**
     * vodId : MOV5861d557d9461c0f2387f4ac
     */

    private String vodId;

    public String getVodId() {
        return vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    @Override
    public String toString() {
        return "SmallVideoItemBean{" +
                "vodId='" + vodId + '\'' +
                '}';
    }
}
