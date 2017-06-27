package com.guanghua.ln.bean;

/**
 * Created by Administrator on 2017/5/31 0031.
 */

public class UserLauncherBean {

    private String UserName;
    private String Platform;
    private String User32Key;
    private String GroupId;
    private String PassWord;
    private String Multicast;
    private String TimeStamp;
    private String AreaCode;

    private static UserLauncherBean sUserLauncherBean;

    public static synchronized UserLauncherBean getInstance(){
        if (sUserLauncherBean == null){
            sUserLauncherBean = new UserLauncherBean();
        }
        return sUserLauncherBean;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getUser32Key() {
        return User32Key;
    }

    public void setUser32Key(String user32Key) {
        User32Key = user32Key;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getMulticast() {
        return Multicast;
    }

    public void setMulticast(String multicast) {
        Multicast = multicast;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public static UserLauncherBean getUserLauncherBean() {
        return sUserLauncherBean;
    }

    public static void setUserLauncherBean(UserLauncherBean userLauncherBean) {
        sUserLauncherBean = userLauncherBean;
    }

    @Override
    public String toString() {
        return "UserLauncherBean{" +
                "UserName='" + UserName + '\'' +
                ", Platform='" + Platform + '\'' +
                ", User32Key='" + User32Key + '\'' +
                ", GroupId='" + GroupId + '\'' +
                ", PassWord='" + PassWord + '\'' +
                ", Multicast='" + Multicast + '\'' +
                ", TimeStamp='" + TimeStamp + '\'' +
                ", AreaCode='" + AreaCode + '\'' +
                '}';
    }
}
