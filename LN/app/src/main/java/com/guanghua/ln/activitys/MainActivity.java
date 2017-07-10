package com.guanghua.ln.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.MyDialogEnterListener;
import com.guanghua.ln.utils.HiFiDialogTools;
import com.guanghua.ln.utils.LnAIDLGetInfo;
import com.guanghua.ln.utils.LnJSAndroidInteractive;
import com.guanghua.ln.views.LnVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guanghua.ln.utils.LnJSAndroidInteractive.isKeyBackClose;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.jsCloseLayerMethod;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.mHideSmallVideo;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    HiFiDialogTools mHiFiDialogTools = new HiFiDialogTools();

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.smallVideoView)
    LnVideoView mSmallVideoView;
    @BindView(R.id.fl_smallVideo)
    FrameLayout mFrameLayout;
    private WebSettings mWebSettings;
    private boolean mBeginJs=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new LnAIDLGetInfo(MainActivity.this);    //AIDL获取用户信息

        getVebView();
    }

    private void getVebView() {
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        //不加载缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(AppCommonInfo.WEBURL);
        mWebView.addJavascriptInterface(new LnJSAndroidInteractive(MainActivity.this, mFrameLayout,
                mSmallVideoView), "android");
        mWebView.requestFocus();
        mWebView.setScrollContainer(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLAY_VIDEO && resultCode == RESULT_OK) {
//            final String cmboIdsArrayStr = data.getStringExtra("cmboIdsArrayStr");
//            final int currentIndex = data.getIntExtra("currentIndex", 0);
//            final long currentPoint = data.getLongExtra("currentPoint", 0);
//            final boolean playEnd = data.getBooleanExtra("playEnd", false);
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //调用JS中的 函数，当然也可以不传参
//                    if (playEnd) {
//                        mWebView.loadUrl("javascript:androidCallBack()");//播放完成回调js
//                        Log.e(TAG, "run: " + "jsCallBack");
//                    } else {
//                        Log.e(TAG, "fffffrun: " + cmboIdsArrayStr + ":" + currentIndex + ":" + currentPoint);
//                        mWebView.loadUrl("javascript:androidCallShowOrder(" + cmboIdsArrayStr + "," + currentIndex + "," + currentPoint + ")"); //订购回调js
//                    }
//                }
//            });
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String currentURL=mWebView.getOriginalUrl();   //获取当前web页面的URL
        //对当前页面进行判断，如果是首页，点击返回弹出退出提示
        if ((currentURL!=null)&&(currentURL.equals(AppCommonInfo.WEBURL))){
            mHiFiDialogTools.showLeftRightTip(MainActivity.this, "温馨提示", "确认退出" +
                            getString(R.string.app_name) + "？", "再玩一会", "退出",
                    new MyDialogEnterListener() {
                        @Override
                        public void onClickEnter(Dialog dialog, Object object) {
                            if ((int) object == 1) {
                               finish();
                            }
                        }
                    });
            return true;
        }

        //机顶盒的返回键监听是KEYCODE_ESCAPE
        if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK) {
            if (jsCloseLayerMethod != null && !jsCloseLayerMethod.equals("0")) {
                String jsString = "javascript:" + jsCloseLayerMethod;
                mWebView.loadUrl(jsString);
                return true;
            } else if (isKeyBackClose) {
                finish();
            } else if (mWebView.canGoBack()) {
                mWebView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        mWebView.clearCache(true);
    }

}
