package com.guanghua.ln.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanghua.ln.activitys.LnPlayVideoActivity;
import com.guanghua.ln.activitys.MainActivity;
import com.guanghua.ln.activitys.OrderActivity;
import com.guanghua.ln.activitys.VodIDVideoActivity;
import com.guanghua.ln.bean.AuthenticationBean;
import com.guanghua.ln.bean.LnBeanPlayItem;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.RecordIDBean;
import com.guanghua.ln.bean.SmallVideoItemBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnRecordIdService;
import com.guanghua.ln.interfaces.ProgramIDAuthenticationService;
import com.guanghua.ln.interfaces.LnPlayUrlService;
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

    public static ArrayList<String> playTitleList = new ArrayList();
    public static ArrayList<String> playVodIdList = new ArrayList();
    public static ArrayList<String> playTrackIdList = new ArrayList();
    public static ArrayList<String> fileTypeList = new ArrayList();
    public static ArrayList<Integer> pointList = new ArrayList();
    public static List<LnBeanPlayItem> playItemList = new ArrayList<>();;
    public static int playIndex1;

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

    String mUser32Key = UserLauncherBean.getUserLauncherBean().getUser32Key();
    String temptoken = mUser32Key;
//        Log.e(TAG, "getAuthention: "+AppCommonInfo.);

    String spid = "YPPL";
    long time = System.currentTimeMillis();
    String riddle = LnUtils.MD5(System.currentTimeMillis() + "spauth");
    String programid = "";
    WebView mWebView;

    String url = "http://59.46.18.48:99/authbilling/authenticationOtt_spAuthority.do?" +
            "temptoken=" + temptoken + "&programid=" + programid + "&spid=" + spid + "&time=" +
            time + "&riddle=" + riddle;
    private String mResult;



    public LnJSAndroidInteractive() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + ":" + resultCode);
        switch (requestCode) {
            case 0:
                Toast.makeText(this,resultCode+"",Toast.LENGTH_LONG).show();
                Log.e(TAG, "onActivityResult: " + resultCode + ":");
                break;
            default:
                break;
        }
    }

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
        if (mPlatform == null) {
            mPlatform = "GD";
        }
        Retrofit retrofit = new Retrofit.Builder()        //使用Retrofit网络框架进行访问网络
                .baseUrl(AppCommonInfo.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnPlayUrlService lnPlayUrlService = retrofit.create(LnPlayUrlService.class);
        String url = "http://59.46.18.25:99/spplayurl/returnPlayUrl.do?" +
                "type=4&time=" + AppCommonInfo.mTime + "&riddle=" + AppCommonInfo.mRiddle +
                "&platform=" + mPlatform + "&spid=" + AppCommonInfo.SpId + "&contentid=" +
                mPlayVodID + "&begintime=&endtime=";
        Log.e(TAG, "getPlayUrl: " + url);
        Call<LnPlayUrlBean> call = lnPlayUrlService.getPlayUrlInfo(AppCommonInfo.Type,
                AppCommonInfo.mTime, AppCommonInfo.mRiddle, mPlatform,
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
//        Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
//        Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
//        intent.putExtra("playIndex", playIndex);
//        intent.putExtra("playListJsonString", playListJsonString);
//        Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
////        mActivity.startActivityForResult(intent, PLAY_VIDEO);
//        mActivity.startActivity(intent);
        getDatas(playIndex,playListJsonString);
//        getAuthenticaton(playIndex, playListJsonString);
        authentication(playIndex, playListJsonString);
    }

    @JavascriptInterface
    public void startTradkIDVideo(String playListJsonString) {
        Log.e(TAG, "startTradkIDVideo: " + playListJsonString);
        Intent intent = new Intent(mContext, VodIDVideoActivity.class);
        intent.putExtra("startVodIDVideo", playListJsonString);
        Log.e(TAG, "startPlayVideo:" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
        mActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void searchState(boolean searchResult) {
        Log.e(TAG, "searchState: " + searchResult);
        mSearchResultState = searchResult;
    }

    @JavascriptInterface
    public void getProductAuthentication(){

    }
    @JavascriptInterface
    public void getProgramAuthentication(){}


    @JavascriptInterface
    public void getAuthentication(int playIndex, String playListJsonString) {
        Log.e(TAG, "getAuthentication: ");
        getAuthenticaton(playIndex, playListJsonString);
    }

    /**
     * 产品鉴权
     * 测试网可用的产品鉴权ID：240001310 和 240001308
     */
    public void getAuthenticaton(final int playIndex, final String playListJsonString) {



        programid = playVodIdList.get(playIndex);    //获取视频ID

        programid = "240001310";
//        programid = "240001308";
//        programid = "PRO0000000296";
        String trackID = playTrackIdList.get(playIndex);

        getRecordID(trackID);

    }

    private void getDatas(int playIndex, String playListJsonString) {
        playIndex1 = playIndex;

        Log.e(TAG, "onCreate: " + temptoken);
        Log.e(TAG, "getAuthention: " + url);
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

    }

    private void authentication(final int playIndex, final String playListJsonString) {
//        programid = "240001310";
//        programid = "240001308";
        programid = "PRO0000000296";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://59.46.18.48:99/authbilling/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProgramIDAuthenticationService programIDAuthenticationService = retrofit.create(ProgramIDAuthenticationService.class);
        Call<AuthenticationBean> authenticationBeanCall = programIDAuthenticationService.getResult(
                temptoken, programid, spid, time, riddle);
//        ProductIDAuthenticationService productIDAuthenticationService=retrofit.create(ProductIDAuthenticationService.class);
//        Call<AuthenticationBean> authenticationBeanCall = productIDAuthenticationService.getResult(
//                temptoken, programid, spid, time, riddle);
        authenticationBeanCall.enqueue(new Callback<AuthenticationBean>() {
            @Override
            public void onResponse(Call<AuthenticationBean> call, Response<AuthenticationBean> response) {
                Log.e(TAG, "onResponse:response "+response.toString());
                AuthenticationBean authenticationBean=new AuthenticationBean();
                authenticationBean=response.body();
                authenticationBean.getResult();
                mResult = authenticationBean.getResult();
                Log.e(TAG, "onResponse: mResult"+mResult);
                if (mResult.equals("1")) {//1.跳转订购页面
                    Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
                    Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
//                    intent.putExtra("playIndex", playIndex);
//                    intent.putExtra("playListJsonString", playListJsonString);
//                    Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
                    mActivity.startActivity(intent);

//                    goToOrderActivity();

//                    Intent intent = new Intent(mActivity, OrderActivity.class);
//                    intent.putExtra("playIndex", playIndex);
//                    intent.putExtra("playListJsonString", playListJsonString);
//                    mActivity.startActivity(intent);
                } else if (mResult.equals("2")) {//2.跳转播放页面
                    Log.e(TAG, "startPlayVideo: 52上学调取startPlayVideo");
                    Intent intent = new Intent(mContext, LnPlayVideoActivity.class);
                    intent.putExtra("playIndex", playIndex);
                    intent.putExtra("playListJsonString", playListJsonString);
                    Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
                    mActivity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<AuthenticationBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + "访问网络失败！");
            }
        });
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
        intent.putExtra("videoname", "变形金刚5");
        intent.putExtra("desc","晚上一起去看变形金刚吧，亲！");
//        intent.putExtra("desc",programid);
//        intent.putExtra("contentid","MOV57ce95781170aa34867effa8");
        intent.putExtra("contentid", "PRO56b454431170126b36ef96fc");
        intent.putExtra("spid", "YPPL");
        intent.putExtra("contenttype", "1");
        intent.putExtras(intent);
        ((AppCompatActivity)mContext).startActivityForResult(intent, 0);

    }


    /**
     * 获取播放RecordID
//
     * @param trackID*/
    private void getRecordID(String trackID) {
//        Log.e(TAG, "onResponse: " + mTrackID + ":" + mUserName);
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

                    try{
                        Log.e(TAG, "onResponse: "+recordIDBean.toString());
//                    mRecordID = recordIDBean.getRecordId();
                        int productIDSize=recordIDBean.getData().getProducts().size();
                        StringBuffer productID=new StringBuffer();
                        for (int i=0;i<productIDSize;i++){
                                productID.append(recordIDBean.getData().getProducts().get(i)+",");
                        }
                    Log.e(TAG, "onResponse:ID " + productID.substring(0,productID.length()-1));
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
//                authentication(playIndex, playListJsonString);

            }

            @Override
            public void onFailure(Call<RecordIDBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 获取RecordID请求网络失败"+t.toString());
            }
        });
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
