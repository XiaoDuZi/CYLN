package com.guanghua.ln.interfaces;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class TrackIDBean {

    /**
     * name : 两只老虎
     * trackId : 5
     * vodId :
     * point : 6
     */

    private String name;
    private String trackId;
    private String vodId;
    private String point;

    public static TrackIDBean objectFromData(String str) {

        return new Gson().fromJson(str, TrackIDBean.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getVodId() {
        return vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
