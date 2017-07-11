package com.guanghua.ln.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2017/6/23 0023.
 */

public class RecordIDBean {
    /**
     * recordId : 557
     * code : 0
     * msg : 添加播放记录成功
     * data : {"name":"金字塔原则","id":1,"mp3Url":"","flacUrl":"","vodId":"MOV5861d557d9461c0f2387f4ac","duration":"","cmboIds":[],"fileType":"1","bigImg":"/utvgo_big/20170627024912193.jpg","smallImg":"/utvgo_small/201706270249137.jpg","artistNames":"","ifCollection":1,"collectionId":0,"isfree":0,"userCode":"201"}
     */

    private int recordId;
    private String code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : 金字塔原则
         * id : 1
         * mp3Url :
         * flacUrl :
         * vodId : MOV5861d557d9461c0f2387f4ac
         * duration :
         * cmboIds : []
         * fileType : 1
         * bigImg : /utvgo_big/20170627024912193.jpg
         * smallImg : /utvgo_small/201706270249137.jpg
         * artistNames :
         * ifCollection : 1
         * collectionId : 0
         * isfree : 0
         * userCode : 201
         */

        private String name;
        private int id;
        private String mp3Url;
        private String flacUrl;
        private String vodId;
        private String duration;
        private String fileType;
        private String bigImg;
        private String smallImg;
        private String artistNames;
        private int ifCollection;
        private int collectionId;
        private int isfree;
        private String userCode;
        private List<?> cmboIds;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMp3Url() {
            return mp3Url;
        }

        public void setMp3Url(String mp3Url) {
            this.mp3Url = mp3Url;
        }

        public String getFlacUrl() {
            return flacUrl;
        }

        public void setFlacUrl(String flacUrl) {
            this.flacUrl = flacUrl;
        }

        public String getVodId() {
            return vodId;
        }

        public void setVodId(String vodId) {
            this.vodId = vodId;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getBigImg() {
            return bigImg;
        }

        public void setBigImg(String bigImg) {
            this.bigImg = bigImg;
        }

        public String getSmallImg() {
            return smallImg;
        }

        public void setSmallImg(String smallImg) {
            this.smallImg = smallImg;
        }

        public String getArtistNames() {
            return artistNames;
        }

        public void setArtistNames(String artistNames) {
            this.artistNames = artistNames;
        }

        public int getIfCollection() {
            return ifCollection;
        }

        public void setIfCollection(int ifCollection) {
            this.ifCollection = ifCollection;
        }

        public int getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(int collectionId) {
            this.collectionId = collectionId;
        }

        public int getIsfree() {
            return isfree;
        }

        public void setIsfree(int isfree) {
            this.isfree = isfree;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public List<?> getCmboIds() {
            return cmboIds;
        }

        public void setCmboIds(List<?> cmboIds) {
            this.cmboIds = cmboIds;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", mp3Url='" + mp3Url + '\'' +
                    ", flacUrl='" + flacUrl + '\'' +
                    ", vodId='" + vodId + '\'' +
                    ", duration='" + duration + '\'' +
                    ", fileType='" + fileType + '\'' +
                    ", bigImg='" + bigImg + '\'' +
                    ", smallImg='" + smallImg + '\'' +
                    ", artistNames='" + artistNames + '\'' +
                    ", ifCollection=" + ifCollection +
                    ", collectionId=" + collectionId +
                    ", isfree=" + isfree +
                    ", userCode='" + userCode + '\'' +
                    ", cmboIds=" + cmboIds +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RecordIDBean{" +
                "recordId=" + recordId +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    //
//    /**
//     * recordId : 415749
//     * code : 500
//     * msg : 系统异常
//     */
//
//    private int recordId;
//    private String code;
//    private String msg;
//
//    public static RecordIDBean objectFromData(String str) {
//
//        return new Gson().fromJson(str, RecordIDBean.class);
//    }
//
//    public int getRecordId() {
//        return recordId;
//    }
//
//    public void setRecordId(int recordId) {
//        this.recordId = recordId;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    @Override
//    public String toString() {
//        return "RecordIDBean{" +
//                "recordId=" + recordId +
//                ", code='" + code + '\'' +
//                ", msg='" + msg + '\'' +
//                '}';
//    }
}
