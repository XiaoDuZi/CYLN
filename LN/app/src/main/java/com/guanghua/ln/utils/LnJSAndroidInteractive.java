package com.guanghua.ln.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanghua.ln.R;
import com.guanghua.ln.activitys.LnPlayVideoActivity;
import com.guanghua.ln.activitys.MainActivity;
import com.guanghua.ln.activitys.PlayListActivity;
import com.guanghua.ln.activitys.VodIDVideoActivity;
import com.guanghua.ln.bean.AuthenticationBean;
import com.guanghua.ln.bean.LnBeanPlayItem;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.RecordIDBean;
import com.guanghua.ln.bean.SmallVideoItemBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnPlayUrlService;
import com.guanghua.ln.interfaces.LnRecordIdService;
import com.guanghua.ln.interfaces.ProductIDAuthenticationService;
import com.guanghua.ln.interfaces.ProgramIDAuthenticationService;
import com.guanghua.ln.interfaces.TrackIDBean;
import com.guanghua.ln.views.LnVideoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2017/5/21 0021.
 */

public class LnJSAndroidInteractive extends MainActivity
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "LnJSAndroidInteractive";

//    private ArrayList<String> playTitleList = new ArrayList();
//    private ArrayList<String> playVodIdList = new ArrayList();
//    private ArrayList<String> playTrackIdList = new ArrayList();
//    private ArrayList<String> fileTypeList = new ArrayList();
//    private ArrayList<Integer> pointList = new ArrayList();
    /**
     * 列表播放数据
     */
    public static ArrayList<String> playTitleList = new ArrayList();
    public static ArrayList<String> playVodIdList = new ArrayList();
    public static ArrayList<String> playTrackIdList = new ArrayList();
    public static ArrayList<String> fileTypeList = new ArrayList();
    public static ArrayList<Integer> pointList = new ArrayList();
    public static List<LnBeanPlayItem> playItemList = new ArrayList<>();
    public static int playIndex1;
    /**
     * 历史播放记录数据
     */
    public static List<TrackIDBean> mPlayHistoryItemList = new ArrayList<>();
    public static ArrayList<String> playHistoryTitleList = new ArrayList();
    public static ArrayList<String> playHistoryTrackIdList = new ArrayList();
    public static ArrayList<String> historyPointList = new ArrayList();

    private Context mContext;
    private Activity mActivity;
    private FrameLayout mFrameLayout;
    private LnVideoView mLnVideoView;

    public static String jsCloseLayerMethod;
    public static boolean isKeyBackClose = false;

    public static final int PLAY_VIDEO = 1000;

    private String mPlatform;

    HiFiDialogTools mHiFiDialogTools = new HiFiDialogTools();
    private List<String> mVodid;
    public static boolean mHideSmallVideo;
    private String mPlayVodID;
    private int i = 0;
    public static boolean mSearchResultState = true;  //搜索结果状态，ture搜索到结果，false没有搜索到结果

//    String mUser32Key = UserLauncherBean.getUserLauncherBean().getUser32Key();


//    long time = System.currentTimeMillis();
//    String riddle = LnUtils.MD5(System.currentTimeMillis() + "spauth");
    String programid;
    WebView mWebView;

    private String mResult;
    private String mTrackID;
    private String mProductID;
    public static int mRecordID;
    public static String mContentId;
    public static String mToActivity;
    private int mIsFree;
    private String mUserCode;

    public LnJSAndroidInteractive(Context context, FrameLayout frameLayout, LnVideoView lnVideoView,
                                  WebView webView) {
        mContext = context;
        mActivity = (Activity) mContext;
        mFrameLayout = frameLayout;
        mLnVideoView = lnVideoView;
        mWebView = webView;
    }

    /**
     * 隐藏小窗口播放视频
     */
    @JavascriptInterface
    public void goneSmallVideo() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.setVisibility(View.GONE);
                mLnVideoView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示小窗口视频播放
     */
    @JavascriptInterface
    public void showSmallVideo(String vodID) {
        Log.e(TAG, "showSmallVideo: ");
        mPlatform = AppCommonInfo.Platform;
        getVodId(vodID);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.setVisibility(View.VISIBLE);
                mLnVideoView.setVisibility(View.VISIBLE);
                mLnVideoView.setFocusable(false);
                mLnVideoView.clearFocus();
                if (mPlayVodID == null) {
                    mPlayVodID = mVodid.get(0);
                    getPlayUrl();
                } else {
                    mLnVideoView.start();
                }
            }
        });

    }


    private void getPlayUrl() {
        long mTime = System.currentTimeMillis();
        String mRiddle = LnUtils.MD5(mTime+AppCommonInfo.PLAY_KEY);
        if (mPlatform == null) {
            mPlatform = "GD";
        }
        Retrofit retrofit = new Retrofit.Builder()        //使用Retrofit网络框架进行访问网络
                .baseUrl(AppCommonInfo.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnPlayUrlService lnPlayUrlService = retrofit.create(LnPlayUrlService.class);

        Call<LnPlayUrlBean> call = lnPlayUrlService.getPlayUrlInfo(AppCommonInfo.Type,
                mTime,mRiddle, mPlatform,
                AppCommonInfo.SpId, mPlayVodID, "", "");
        call.enqueue(new Callback<LnPlayUrlBean>() {
            @Override
            public void onResponse(Call<LnPlayUrlBean> call, Response<LnPlayUrlBean> response) {
                final LnPlayUrlBean mLnPlayUrlBean = response.body();
                Log.e(TAG, "onResponse: " + response.toString());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playVideo(mLnPlayUrlBean);       //播放视频
                    }
                });
            }

            @Override
            public void onFailure(Call<LnPlayUrlBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 请求视频链接失败！");
            }
        });
    }

    private void playVideo(LnPlayUrlBean lnPlayUrlBean) {
        if (lnPlayUrlBean.getPlayUrl() == null)
            return;
        try {
            Uri playUrl = Uri.parse(lnPlayUrlBean.getPlayUrl());
            if (playUrl.toString().equals("该内容无播放地址!")) {
                switch (mPlatform) {
                    case "ZX":
                        mPlatform = "GD";
                        getPlayUrl();
                        break;
                    case "GD":
                        mPlatform = "HW";
                        getPlayUrl();
                        //正式地址
                        break;
                    case "HW":
                        mPlatform = "ZX";
                        getPlayUrl();
                        break;
                }
            } else {
                //正式地址
                beginPlayVideo(playUrl);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void beginPlayVideo(Uri playUrl) {
        //正式地址
        mLnVideoView.setVideoPath(playUrl.toString());

        mLnVideoView.setOnPreparedListener(this);
        mLnVideoView.requestFocus();
    }

    /**
     * 播放首页小窗口视频获取视频播放ID
     * @param vodID
     */
    private void getVodId(String vodID) {
        List<SmallVideoItemBean> smallVideoItemBeanList = new ArrayList<>();
        Gson gson = new Gson();
        smallVideoItemBeanList = (List<SmallVideoItemBean>) gson.fromJson(vodID,
                new TypeToken<List<SmallVideoItemBean>>() {
                }.getType());
        mVodid = new ArrayList<>();
        for (int i = 0; i < smallVideoItemBeanList.size(); i++) {
            mVodid.add(smallVideoItemBeanList.get(i).getVodId());
        }
    }

    /**
     * 调用视频列表页面
     */
    @JavascriptInterface
    public void getPlayListActivity(String albumId){
        Log.e(TAG, "getPlayListActivity: "+albumId);
        PlayListActivity.actionStart(mActivity,albumId);
    }

    //停止播放小窗口视频
    @JavascriptInterface
    public void stopSmallVideo() {
        mLnVideoView.stopPlayback();
    }

    //关闭弹层
    @JavascriptInterface
    public void onClickBack(final String strCallback) {
        Log.e(TAG, "onClickBack: ");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsCloseLayerMethod = strCallback;
            }
        });
    }

    //按返回键关闭WebView
    @JavascriptInterface
    public void onClickKeyBack() {
        android.util.Log.e(TAG, "onClickKeyBack: a");
//        XLog.d(TAG, "onClickKeyBack ");
        isKeyBackClose = true;
    }

    @JavascriptInterface
    public String getKeyNo() {
        //web页面通过这个方法，获取智能卡号
        return UserLauncherBean.getInstance().getUserName();
    }

    @JavascriptInterface
    public String get32Key(){
        return UserLauncherBean.getInstance().getUser32Key();
    }

    @JavascriptInterface
    public void startPlayVideo(int playIndex, String playListJsonString) {
        mToActivity = "LnPlayVideoActivity";
//        Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
//        Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
//        intent.putExtra("playIndex", playIndex);
//        intent.putExtra("playListJsonString", playListJsonString);
//        Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
////        mActivity.startActivityForResult(intent, PLAY_VIDEO);
//        mActivity.startActivity(intent);
        getDatas(playIndex, playListJsonString);//获取前端传来数据
        getProductID(mTrackID);                  //获取产品ID

    }

    @JavascriptInterface
    public void startTradkIDVideo(String playListJsonString) {
        mToActivity = "VodIDVideoActivity";
        getHistoryDatas(playListJsonString);
        getProductID(mTrackID);                  //获取产品ID
    }

    /**
     * 视频列表页订购，鉴权
     * @param productID
     */
    @JavascriptInterface
    public void videoListOrderAuthentication(String productID){
        Log.e(TAG, "videoListOrderAuthentication: "+productID);
        authentication(productID);
    }

    @JavascriptInterface
    public void searchState(boolean searchResult) {
        Log.e(TAG, "searchState: " + searchResult);
        mSearchResultState = searchResult;
    }

    private void getHistoryDatas(String playListJsonString) {

        Log.e(TAG, "getHistoryDatas: "+playListJsonString);

        mPlayHistoryItemList.clear();
        playHistoryTrackIdList.clear();
        playHistoryTitleList.clear();
        historyPointList.clear();

        Gson gson = new Gson();
        mPlayHistoryItemList = gson.fromJson(playListJsonString, new TypeToken<List<TrackIDBean>>() {
        }.getType());

        if (mPlayHistoryItemList.isEmpty()) {
            return;
        }
        for (int i = 0; i < mPlayHistoryItemList.size(); i++) {
            TrackIDBean tackIDBean=new TrackIDBean();
            tackIDBean=mPlayHistoryItemList.get(i);
            Log.e(TAG, "getHistoryDatas:trackID "+tackIDBean.toString());
            TrackIDBean playItem = mPlayHistoryItemList.get(i);
            Log.e(TAG, "initDatas: "+playItem+":"+playItem.getName());
            playHistoryTitleList.add(playItem.getName());
            historyPointList.add(playItem.getPoint());
            playHistoryTrackIdList.add(playItem.getTrackId() + "");
        }
        mTrackID=playHistoryTrackIdList.get(0);
    }



    private void getDatas(int playIndex, String playListJsonString) {
        playIndex1 = playIndex;

        playItemList.clear();
        playTitleList.clear();
        playTrackIdList.clear();
        playVodIdList.clear();
        pointList.clear();
        fileTypeList.clear();

        Gson gson = new Gson();
        playItemList = gson.fromJson(playListJsonString, new TypeToken<List<LnBeanPlayItem>>() {
        }.getType());

        if (playItemList.isEmpty()) {
            return;
        }
        for (int i = 0; i < playItemList.size(); i++) {
            LnBeanPlayItem playItem = playItemList.get(i);
            Log.e(TAG, "initDatas: " + playItem + ":" + playItem.getName());
            playTitleList.add(playItem.getName());
            playVodIdList.add(playItem.getVodId().trim());
            fileTypeList.add(playItem.getFileType());
            pointList.add(playItem.getInitPoint());
            playTrackIdList.add(playItem.getTrackId() + "");
        }
        mTrackID = playTrackIdList.get(playIndex);

    }

    private void authentication(String productID) {

        long time = System.currentTimeMillis();
        String riddle = LnUtils.MD5(time+AppCommonInfo.PRODUCT_PROGRAM_KEY);
        String temptoken = UserLauncherBean.getUserLauncherBean().getUser32Key();

//        programid = "240001310";
//        programid = "240001308";
//        programid = "240001312";
//        240001311 240001312
//        programid = "PRO0000000296";
//        programid = "PRO0000000297";
        programid = productID;
//        240001314  240001313 这两个是新建立的自动续订的产品
//        240001315  240001316 240001317 240001318 240001319 240001320
//        programid="240001315";

        Log.e(TAG, "authentication: "+productID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://59.46.18.48:99/authbilling/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        ProgramIDAuthenticationService programIDAuthenticationService = retrofit.create(ProgramIDAuthenticationService.class);
//        Call<AuthenticationBean> authenticationBeanCall = programIDAuthenticationService.getResult(
//                temptoken, programid, spid, time, riddle);
        ProductIDAuthenticationService productIDAuthenticationService=retrofit.create(ProductIDAuthenticationService.class);
        Call<AuthenticationBean> authenticationBeanCall = productIDAuthenticationService.getResult(
                temptoken, programid,AppCommonInfo.SpId,time,riddle);
        authenticationBeanCall.enqueue(new Callback<AuthenticationBean>() {
            @Override
            public void onResponse(Call<AuthenticationBean> call, Response<AuthenticationBean> response) {
                Log.e(TAG, "onResponse:response " + response.toString());
                AuthenticationBean authenticationBean = new AuthenticationBean();
                authenticationBean = response.body();
                authenticationBean.getResult();
                mResult = authenticationBean.getResult();
                Log.e(TAG, "onResponse: mResult" + mResult);
                if (mResult.equals("1")) {//1.跳转订购页面
//                    Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
//                    Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
//                    intent.putExtra("playIndex", playIndex);
//                    intent.putExtra("playListJsonString", playListJsonString);
//                    Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
//                    mActivity.startActivity(intent);
                    goToOrderActivity();
                } else if (mResult.equals("2")) {//2.跳转播放页面
                    Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
                    jumpToPlayActivity();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + "访问网络失败！");
            }
        });
    }

    private void jumpToPlayActivity() {
        if (mToActivity.equals("LnPlayVideoActivity")){
            Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
            mActivity.startActivity(intent);
        }else if (mToActivity.equals("VodIDVideoActivity")){
            Intent intent = new Intent(mContext, VodIDVideoActivity.class);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 跳转订购页面
     */
    private void goToOrderActivity() {
        //第一个参数是Activity所在的package包名，第二个参数是完整的Class类名（包括包路径）
        ComponentName componentName = new ComponentName("com.widgetdo.ottboxforgx",
                "com.widgetdo.ottboxforgx.activity.OrderListOutwardActivity");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.putExtra("videoname","变形记");
        intent.putExtra("desc", "你好辽宁");
//        intent.putExtra("desc",programid);
//        intent.putExtra("contentid","MOV57ce95781170aa34867effa8");
//        intent.putExtra("contentid", "PRO56b454431170126b36ef96fc");
        Log.e(TAG, "goToOrderActivity: "+programid);
        intent.putExtra("contentid",programid);
        intent.putExtra("spid",AppCommonInfo.SpId);
        intent.putExtra("contenttype", "2");//视频类型：1：视频；2：游戏
        intent.putExtras(intent);
        ((AppCompatActivity) mContext).startActivityForResult(intent, 0);

    }
    /**
     * 获取播放RecordID
     * //
     *
     * @param trackID
     */
    private void getProductID(String trackID) {
        Log.e(TAG, "onResponse: " + mTrackID );
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.RECORDID_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnRecordIdService mLnRecordIdService = mRetrofit.create(LnRecordIdService.class);
        Call<RecordIDBean> mRecordIDBeanCall = mLnRecordIdService.getRecordID(trackID,
                UserLauncherBean.getInstance().getUserName());
        mRecordIDBeanCall.enqueue(new Callback<RecordIDBean>() {

            @Override
            public void onResponse(Call<RecordIDBean> call, Response<RecordIDBean> response) {
                Log.e(TAG, "onResponse: RecordID获取访问网络成功！" + response);
                RecordIDBean recordIDBean = new RecordIDBean();
                recordIDBean = response.body();
                try {
                    int productIDSize = recordIDBean.getData().getProducts().size();
                    StringBuffer products = new StringBuffer();
                    for (int i = 0; i < productIDSize; i++) {
                        products.append(recordIDBean.getData().getProducts().get(i) + ",");
                    }
                    mRecordID = recordIDBean.getRecordId();        //提供给VodIDVideoActivity使用
                    mContentId = recordIDBean.getData().getVodId();//提供给VodIDVideoActivity使用
                    //0:收费；1：免费
                    mIsFree = recordIDBean.getData().getIsfree();
                    //200：白名单以及已订购；201：未订购；需要鉴权；202：黑名单
                    mUserCode = recordIDBean.getData().getUserCode();
                    mProductID = products.substring(0, products.length() - 1);
                    Log.e(TAG, "onResponse: UserCode:"+mUserCode);
                    userAccountState();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RecordIDBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 获取RecordID请求网络失败" + t.toString());
            }
        });
    }

    /**
     * 用户账号状态判断
     */
    private void userAccountState() {
        if (mUserCode.equals("202")){  //黑名单判断
            AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.blacklist_info)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog= builder.create();
            alertDialog.show();
            return;
        }else if (mUserCode.equals("200")){//白名单判断
            jumpToPlayActivity();
        }else if (mUserCode.equals("201")){//未定购
            if (mIsFree==0){
                Log.e(TAG, "onResponse: 0:mIsFree"+mIsFree);
                authentication(mProductID);//鉴权
            }else if (mIsFree==1){
                Log.e(TAG, "onResponse: mIsFree"+mIsFree);
                jumpToPlayActivity();
            }
        }
    }

//    //退出app
//    @JavascriptInterface
//    public void exitApp() {
//        Log.e(TAG, "exitApp: ");
//        mHiFiDialogTools.showLeftRightTip(mContext, "温馨提示", "确认退出" +
//                        mContext.getString(R.string.app_name) + "？", "再玩一会", "退出",
//                new MyDialogEnterListener() {
//                    @Override
//                    public void onClickEnter(Dialog dialog, Object object) {
//                        if ((int) object == 1) {
//                            mActivity.finish();
//                        }
//                    }
//                });
//    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp.isPlaying())
            mp.reset();
        mp.start();
        mp.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        i++;
        if (i >= mVodid.size()) {
            i = 0;
            mPlayVodID = mVodid.get(i);
        } else {
            mPlayVodID = mVodid.get(i);
        }
        getPlayUrl();
    }
}
