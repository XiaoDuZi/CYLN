package com.guanghua.ln.common;

import com.guanghua.ln.bean.UserLauncherBean;

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
    public static String URL = "http://59.46.18.18";
    //阿里云
//    public static String URL = "http://120.76.221.222";
    //河北
//    public static String URL="http:192.168.44.200";

    public static String WEBURL = URL + "/ott/";
    /**
     * 首页
     */
    public static String INDEX_URL = WEBURL + "index.html?menuPos=";

    /**
     * 播放视频请求头部
     * 生产环境接口IP是59.46.18.5
     * 测试环境接口IP是59.46.18.25
     */
    public static String VIDEO_URL = "http://59.46.18.25:99/";

    public static String BASEURL = VIDEO_URL + "spplayurl/";

    /**
     * 播放列表
     */
    //辽宁
    public static String PLAY_LIST=URL+"/LNTVWeb_edu/hifi/hifiData/";
    //阿里云测试网
//    public static String PLAY_LIST = URL + "/CQTVWeb_edu/hifi/hifiData/";

    //辽宁
    public static String PLAY_RECORD_BASEURL = URL + "/LNTVWeb_edu/tvutvgo/";
//    阿里云
//    public static String PLAY_RECORD_BASEURL = URL + "/CQTVWeb_edu/tvutvgo/";

    /**
     * 播放列表图片URL
     * 例：http://59.46.18.18:81/news/uploadFile//utvgo_track/small_img/cp00010203070002_B.jpg
     */
    public static String PLAY_LIST_IMAGE_URL =URL+":81/news/uploadFile/";

    /**
     * 获取RecordId
     */
//    辽宁
    public static String RECORDID_BASEURL = URL + "/LNTVWeb_edu/hifi/hifiData/";
//    阿里云
//    public static String RECORDID_BASEURL = URL + "/CQTVWeb_edu/hifi/hifiData/";


    //视频播放类型 1：直播；2：回看；4：点播（默认为点播）
    public static int Type = 4;

    //第三方sp的id
//    public static String SpId = "YPPL";
    //sp ID：
    public static String SpId = "CQGH";

    //首页
    public static String INDEX_HTML = "&backUrl=index.html";

    //列表
    public static String LIST_HTML = "&backUrl=list.html";

    //调用平台:HW:华为；ZX:中兴；GD:广电
    public static String Platform = UserLauncherBean.getInstance().getPlatform();   //获取平台

    //产品，内容鉴权加密串，Key值
    public static String PRODUCT_PROGRAM_KEY = "spauth";

    //播放接口链接加密串，Key值
    public static String PLAY_KEY = "besto";

    public static boolean sInPlayVideo = false;
}
