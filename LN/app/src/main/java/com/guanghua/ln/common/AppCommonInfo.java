package com.guanghua.ln.common;

import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.utils.LnMD5Utils;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class AppCommonInfo {

    /**
     * Web加载项目地址
     * http://120.76.221.222/
     * "http://192.168.16.49/ott"
     * "http://192.168.88.10/ott"
     * "file:///android_asset/index.html"
     * "file:///android_asset/ott_6.5/album_video_list.html"
     */
    //辽宁测试网
    public static String URL="http://59.46.18.18/";
    //阿里云
//    public static String URL="http://120.76.221.222/";

    public static String WEBURL =URL+"ott/";
    /**
     * 首页
     */
    public static String INDEX_URL=WEBURL+"index.html?menuPos=";

    /**
     * 播放视频请求头部
     * 生产环境接口IP是59.46.18.5
     * 测试环境接口IP是59.46.18.25
     */
    public static String VIDEO_URL="http://59.46.18.5:99/";

    public static String BASEURL =VIDEO_URL+"spplayurl/";

    public static String PLAY_RECORD_BASEURL = URL+"LNTVWeb_edu/tvutvgo/";
    /**
     * 获取RecordId
     */
    public static String RECORDID_BASEURL = URL+"LNTVWeb_edu/hifi/hifiData/";

    //视频播放类型 1：直播；2：回看；4：点播（默认为点播）
    public static int Type = 4;

    //第三方sp的id
    public static String SpId = "YPPL";

    //首页
    public static String INDEX_HTML="&backUrl=index.html";

    //列表
    public static String LIST_HTML="&backUrl=list.html";

    //调用平台:HW:华为；ZX:中兴；GD:广电
    public static String Platform = UserLauncherBean.getInstance().getPlatform();   //获取平台

    public static long mTime = System.currentTimeMillis();                                     //获取时间戳
    public static String mRiddle = LnMD5Utils.MD5(System.currentTimeMillis() + "besto");           //加密串加密串（时间戳+key的md5值），
}
