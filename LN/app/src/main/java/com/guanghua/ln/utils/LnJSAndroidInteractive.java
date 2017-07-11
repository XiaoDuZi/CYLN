package com.guanghua.ln.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanghua.ln.activitys.LnPlayVideoActivity;
import com.guanghua.ln.activitys.R;
import com.guanghua.ln.activitys.VodIDVideoActivity;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.SmallVideoItemBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnPlayUrlService;
import com.guanghua.ln.interfaces.MyDialogEnterListener;
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

public class LnJSAndroidInteractive implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "LnJSAndroidInteractive";

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
    private int i=0;

    public LnJSAndroidInteractive() {
    }

    public LnJSAndroidInteractive(Context context, FrameLayout frameLayout, LnVideoView lnVideoView) {
        mContext = context;
        mActivity = (Activity) mContext;
        mFrameLayout = frameLayout;
        mLnVideoView = lnVideoView;
    }

    //隐藏小窗口播放视频
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
    //显示小窗口视频播放
    @JavascriptInterface
    public void showSmallVideo(String vodID) {
        Log.e(TAG, "showSmallVideo: ");
        mPlatform=AppCommonInfo.Platform;
        getVodId(vodID);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.setVisibility(View.VISIBLE);
                mLnVideoView.setVisibility(View.VISIBLE);
                mLnVideoView.setFocusable(false);
                mLnVideoView.clearFocus();
                if (mPlayVodID==null){
                    Log.e(TAG, "run: "+mPlayVodID);
                    mPlayVodID=mVodid.get(0);
                    getPlayUrl();
                }else {
                    Log.e(TAG, "run: start");
                    mLnVideoView.start();
                }
            }
        });

    }


    private void getPlayUrl() {
        if (mPlatform==null){
            mPlatform="GD";
        }
        Retrofit retrofit = new Retrofit.Builder()        //使用Retrofit网络框架进行访问网络
                .baseUrl(AppCommonInfo.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnPlayUrlService lnPlayUrlService = retrofit.create(LnPlayUrlService.class);
        String url = "http://59.46.18.25:99/spplayurl/returnPlayUrl.do?" +
                "type=4&time=" + AppCommonInfo.mTime + "&riddle=" + AppCommonInfo.mRiddle +
                "&platform=" +mPlatform+ "&spid="+AppCommonInfo.SpId+"&contentid=" +
                mPlayVodID+ "&begintime=&endtime=";
        Log.e(TAG, "getPlayUrl: "+url);
        Call<LnPlayUrlBean> call = lnPlayUrlService.getPlayUrlInfo(AppCommonInfo.Type,
                AppCommonInfo.mTime,AppCommonInfo.mRiddle,mPlatform,
                AppCommonInfo.SpId,mPlayVodID, "","");
        call.enqueue(new Callback<LnPlayUrlBean>() {
            @Override
            public void onResponse(Call<LnPlayUrlBean> call, Response<LnPlayUrlBean> response) {
                final LnPlayUrlBean mLnPlayUrlBean = response.body();
                Log.e(TAG, "onResponse: "+response.toString());
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
        Log.e(TAG, "playVideo: " + mPlatform);
        //正式地址
        mLnVideoView.setVideoPath(playUrl.toString());

        mLnVideoView.setOnPreparedListener(this);
        mLnVideoView.requestFocus();
//        if (TextUtils.equals(mFileType, "1")) { //视频播放
//            mIvVideoBg.setVisibility(View.GONE);
//        }
    }

    private void getVodId(String vodID) {
        List<SmallVideoItemBean> smallVideoItemBeanList=new ArrayList<>();
        Gson gson=new Gson();
        smallVideoItemBeanList= (List<SmallVideoItemBean>) gson.fromJson(vodID,
                new TypeToken<List<SmallVideoItemBean>>(){}.getType());
        mVodid = new ArrayList<>();
        for (int i=0;i<smallVideoItemBeanList.size();i++){
            mVodid.add(smallVideoItemBeanList.get(i).getVodId());
        }
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
    public void startPlayVideo(int playIndex, String playListJsonString) {
        Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
        Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
        intent.putExtra("playIndex", playIndex);
        intent.putExtra("playListJsonString", playListJsonString);
        Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
        mActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void startVodIDVideo(String playListJsonString) {
        Intent intent = new Intent(mContext,VodIDVideoActivity.class);
        intent.putExtra("startVodIDVideo", playListJsonString);
        Log.e(TAG, "startPlayVideo:" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
        mActivity.startActivity(intent);
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
        Log.e(TAG, "onCompletion: "+mVodid.size());
        i++;
        if (i>=mVodid.size()){
            i=0;
            Log.e(TAG, "onCompletion: "+i);
            mPlayVodID = mVodid.get(i);
        }else {
            mPlayVodID = mVodid.get(i);
        }
        getPlayUrl();
    }
}
