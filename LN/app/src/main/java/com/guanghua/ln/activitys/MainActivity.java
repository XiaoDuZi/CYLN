package com.guanghua.ln.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.utils.GetRecordId;
import com.guanghua.ln.utils.LnAIDLGetInfo;
import com.guanghua.ln.utils.LnJSAndroidInteractive;
import com.guanghua.ln.utils.VideoViewOutlineProvider;
import com.guanghua.ln.views.LnVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.radius;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.isKeyBackClose;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.jsCloseLayerMethod;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.smallVideoView)
    LnVideoView mSmallVideoView;
    @BindView(R.id.fl_smallVideo)
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new LnAIDLGetInfo(MainActivity.this);    //AIDL获取用户信息

        getVebView();
        getSmallVideo();
    }

    private void getSmallVideo() {
        String path = "http://vf1.mtime.cn/Video/2017/02/09/flv/170209204824569974.flv";

        mSmallVideoView.setVideoPath(path);
//        mVideoView.requestFocus();
        mSmallVideoView.start();
    }

    private void getVebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //不加载缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(AppCommonInfo.WEBURL);
//        mWebView.addJavascriptInterface(new LnJSAndroidInteractive(MainActivity.this),"test");
        mWebView.addJavascriptInterface(new LnJSAndroidInteractive(MainActivity.this,mFrameLayout,
                mSmallVideoView), "android");
        mWebView.requestFocus();
//        mWebView.
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
        Log.e(TAG, "onKeyDown: " + keyCode);
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
