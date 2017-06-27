package com.guanghua.ln.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.guanghua.ln.activitys.LnPlayVideoActivity;
import com.guanghua.ln.activitys.R;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.interfaces.MyDialogEnterListener;


/**
 * Created by Administrator on 2017/5/21 0021.
 */

public class LnJSAndroidInteractive {

    private static final String TAG = "LnJSAndroidInteractive";

    private Context mContext;
    private Activity mActivity;

    public static String jsCloseLayerMethod;
    public static boolean isKeyBackClose = false;

    public static final int PLAY_VIDEO = 1000;

    HiFiDialogTools mHiFiDialogTools = new HiFiDialogTools();

    public LnJSAndroidInteractive(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;
    }

    //关闭弹层
    @JavascriptInterface
    public void onClickBack(final String strCallback) {
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

    //退出app
    @JavascriptInterface
    public void exitApp() {
        mHiFiDialogTools.showLeftRightTip(mContext, "温馨提示", "确认退出" +
                        mContext.getString(R.string.app_name) + "？", "再玩一会", "退出",
                new MyDialogEnterListener() {
                    @Override
                    public void onClickEnter(Dialog dialog, Object object) {
                        if ((int) object == 1) {
                            mActivity.finish();
                        }
                    }
                });
    }

    @JavascriptInterface
    public void jump() {
        mContext.startActivity(new Intent(mContext, LnPlayVideoActivity.class));
    }
}
