package com.guanghua.ln.bean;

/**
 * Created by fute on 17/1/bg.
 */

public class LnBeanPlayItem {
    private int trackId;    //课件
    private int initPoint;  //播放点
    private String name;    //课件名字
    private String vodId;   //视频
    private String fileType;//文件类型音频视频

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getInitPoint() {
        return initPoint;
    }

    public void setInitPoint(int initPoint) {
        this.initPoint = initPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVodId() {
        return vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    @Override
    public String toString() {
        return "BeanPlayItem{" +
                "trackId=" + trackId +
                ", initPoint=" + initPoint +
                ", name='" + name + '\'' +
                ", vodId='" + vodId + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
