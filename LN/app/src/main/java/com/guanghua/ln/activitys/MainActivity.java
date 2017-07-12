package com.guanghua.ln.activitys;

import android.app.DialogFragment;
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

import com.guanghua.ln.R;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.fragments.CyDialogFragment;
import com.guanghua.ln.utils.HiFiDialogTools;
import com.guanghua.ln.utils.LnAIDLGetInfo;
import com.guanghua.ln.utils.LnJSAndroidInteractive;
import com.guanghua.ln.views.LnVideoView;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guanghua.ln.utils.LnJSAndroidInteractive.mSearchResultState;


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
    private boolean mBeginJs = false;
//    private String mUrl;

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

        mWebView.setBackgroundColor(getResources().getColor(R.color.transparent));
        mWebView.setBackgroundResource(R.drawable.home_bg);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(AppCommonInfo.WEBURL);
        mWebView.addJavascriptInterface(new LnJSAndroidInteractive(MainActivity.this, mFrameLayout,
                mSmallVideoView), "android");
        mWebView.requestFocus();
        mWebView.clearCache(true);
        mWebView.clearHistory();
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

//        String currentURL = mWebView.getOriginalUrl();   //获取当前web页面的URL
        String currentURL = mWebView.getUrl();  //获取当前web页面的URL
        Log.e(TAG, "onKeyDown: " + currentURL);
        String endUrl = null;//最终URL
        if (currentURL.equals(AppCommonInfo.WEBURL)) {
            endUrl = AppCommonInfo.WEBURL;
        } else {
            try {
                String subUrl = currentURL.substring(currentURL.indexOf("backUrl=") + "backUrl=".length(), currentURL.length());
                //       解码编码     http://blog.csdn.net/junhuahouse/article/details/23087755
//            String   text1  =   java.net.URLEncoder.encode("中国",   "utf-8"); 编码
                String encodeUrl = java.net.URLDecoder.decode(subUrl, "utf-8");
                endUrl = AppCommonInfo.WEBURL + encodeUrl;
                Log.e(TAG, "onKeyDown:1111 " + encodeUrl);
                Log.e(TAG, "onKeyDown:2222 " + endUrl);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //机顶盒的返回键监听是KEYCODE_ESCAPE
        if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSearchResultState){  //判断搜索页面状态，如果是true
                Log.e(TAG, "onKeyDown: "+mSearchResultState);
            }else {
                Log.e(TAG, "onKeyDown: "+mSearchResultState);
//                String call = "javascript:sayHello()";
//                mWebView.loadUrl(call);
                mSearchResultState=true;
                return false;         //如果是false让浏览器获取返回键状态，取消搜索提示
            }
            //对当前页面进行判断，如果是首页，点击返回弹出退出提示
            Log.e(TAG, "onKeyDown:endURL " + endUrl);
            if ((currentURL.equals(AppCommonInfo.INDEX_URL+currentURL.substring(currentURL.length() - 1)))
                    ||currentURL.equals(AppCommonInfo.WEBURL)) {
                Log.e(TAG, "onKeyDown: showExitDialog");
                showExitDialog();

//                mHiFiDialogTools.showLeftRightTip(MainActivity.this, "温馨提示", "确认退出" +
//                                getString(R.string.app_name) + "？", "再玩一会", "退出",
//                        new MyDialogEnterListener() {
//                            @Override
//                            public void onClickEnter(Dialog dialog, Object object) {
//                                if ((int) object == 1) {
//                                    finish();
//                                }
//                            }
//                        });
                return true;
            } else
                if (mWebView.canGoBack()) {
                mWebView.loadUrl(endUrl);
//                int intIndex = currentURL.indexOf(AppCommonInfo.INDEX_HTML);//查找字符
//                int listIndex=currentURL.indexOf(AppCommonInfo.LIST_HTML);
//                if (listIndex==-1){
//                    mWebView.goBack(); // goBack()表示返回WebView的上一页面
//                }else {
////                    mWebView.loadUrl();
//                }
//                if (intIndex == -1) {
//                    mWebView.goBack(); // goBack()表示返回WebView的上一页面
//                } else {
//                    mWebView.loadUrl(AppCommonInfo.INDEX_URL + currentURL.substring(currentURL.length() - 1));
//                }
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示退出对话框
     */
    private void showExitDialog() {
        DialogFragment dialogFragment=CyDialogFragment.newInstance(1);
        dialogFragment.show(getFragmentManager(),"exitDialog");
    }

    @Override
    public void finish() {
        super.finish();
        mWebView.clearCache(true);
    }
}
