package com.guanghua.ln.activitys;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.guanghua.ln.R;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.fragments.CyDialogFragment;
import com.guanghua.ln.utils.LnAIDLGetInfo;
import com.guanghua.ln.utils.LnJSAndroidInteractive;
import com.guanghua.ln.views.LnVideoView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guanghua.ln.utils.DataCleanManager.cleanApplicationData;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.mSearchResultState;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.mToActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.smallVideoView)
    LnVideoView mSmallVideoView;
    @BindView(R.id.fl_smallVideo)
    FrameLayout mFrameLayout;
    private WebSettings mWebSettings;
    private boolean mBeginJs = false;
    private String mWebUrl;
    //    private String mUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new LnAIDLGetInfo(MainActivity.this);    //AIDL获取用户信息

        mWebUrl = getIntent().getStringExtra("CYURL");
        Log.e(TAG, "onCreate: " + mWebUrl);

        if (TextUtils.isEmpty(mWebUrl)) {
            mWebUrl = AppCommonInfo.WEBURL;
        } else if (!mWebUrl.startsWith(AppCommonInfo.WEBURL)) {
            mWebUrl = AppCommonInfo.WEBURL + mWebUrl;
        }
        initViews(savedInstanceState);
        mWebView.addJavascriptInterface(new LnJSAndroidInteractive(MainActivity.this, mFrameLayout,
                mSmallVideoView, mWebView), "android");

        new DownloadImageTask().execute("http://59.46.18.18/ott/app/video_border.png",
                "http://59.46.18.18/ott/app/home_bg.jpg");
    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过第二个参数(文件名)来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), null);
        } catch (IOException e) {
            Log.d("skythinking", e.getMessage());
        }
        if (drawable == null) {
            Log.d("skythinking", "null drawable");
        } else {
            Log.d("skythinking", "not null drawable");
        }

//        compress(Bitmap.CompressFormat.JPEG, 100,drawable);

        return drawable;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, List<Drawable>> {

        protected List<Drawable> doInBackground(String... urls) {
            List<Drawable> drawable = new ArrayList<>();
            for (int i = 0; i < urls.length; i++) {
                drawable.add(loadImageFromNetwork(urls[i]));
            }
            return drawable;
        }

        protected void onPostExecute(List<Drawable> result) {
            for (int i = 0; i < result.size(); i++) {
                mFrameLayout.setForeground(result.get(0));
                mWebView.setBackground(result.get(1));
            }
        }
    }

    private void initViews(Bundle savedInstanceState) {

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //不加载缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setBackgroundColor(getResources().getColor(R.color.transparent));
//        mWebView.setBackgroundResource(R.drawable.home_bg);
//        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.clearCache(true);
        mWebView.requestFocus();
        mWebView.setScrollContainer(false);
//        mWebView.loadUrl("file:///android_asset/ott_6.5/album_video_list.html");
        mWebView.loadUrl(mWebUrl);
//        mWebView.loadUrl("http://125.62.17.137/gupiao/order/orderdetail.html");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        }

    }

    /**
     * @param requestCode 0
     * @param resultCode  1：订购成功；2：订购失败
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == 1) {//返回
                    if (mToActivity.equals("LnPlayVideoActivity")) {
                        Intent intent = new Intent(MainActivity.this, LnPlayVideoActivity.class);
                        startActivity(intent);
                    } else if (mToActivity.equals("VodIDVideoActivity")) {
                        Intent intent = new Intent(MainActivity.this, VodIDVideoActivity.class);
                        startActivity(intent);
                    }
                } else if (resultCode == 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("订购失败！")
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                break;
            default:
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == PLAY_VIDEO && resultCode == RESULT_OK) {
////            final String cmboIdsArrayStr = data.getStringExtra("cmboIdsArrayStr");
////            final int currentIndex = data.getIntExtra("currentIndex", 0);
////            final long currentPoint = data.getLongExtra("currentPoint", 0);
////            final boolean playEnd = data.getBooleanExtra("playEnd", false);
////
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    //调用JS中的 函数，当然也可以不传参
////                    if (playEnd) {
////                        mWebView.loadUrl("javascript:androidCallBack()");//播放完成回调js
////                        Log.e(TAG, "run: " + "jsCallBack");
////                    } else {
////                        Log.e(TAG, "fffffrun: " + cmboIdsArrayStr + ":" + currentIndex + ":" + currentPoint);
////                        mWebView.loadUrl("javascript:androidCallShowOrder(" + cmboIdsArrayStr + "," + currentIndex + "," + currentPoint + ")"); //订购回调js
////                    }
////                }
////            });
////        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        String currentURL = mWebView.getOriginalUrl();   //获取当前web页面的URL
        String currentURL = mWebView.getUrl();  //获取当前web页面的URL
        Log.e(TAG, "onKeyDown:currentUrl: " + currentURL);
        String endUrl = null;//最终URL

        if (currentURL.equals(AppCommonInfo.WEBURL)) {
            endUrl = AppCommonInfo.WEBURL;
        } else if (currentURL.indexOf("backUrl=") != -1) {
            try {
                String subUrl = currentURL.substring(currentURL.indexOf("backUrl=") + "backUrl=".length(), currentURL.length());
                //       解码编码     http://blog.csdn.net/junhuahouse/article/details/23087755
//            String   text1  =   java.net.URLEncoder.encode("中国",   "utf-8"); 编码
                String encodeUrl = java.net.URLDecoder.decode(subUrl, "utf-8");
                endUrl = AppCommonInfo.WEBURL + encodeUrl;
                Log.e(TAG, "onKeyDown:encodeUrl: " + encodeUrl);
                Log.e(TAG, "onKeyDown:endUrl: " + endUrl);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            endUrl = currentURL;
        }

        //机顶盒的返回键监听是KEYCODE_ESCAPE
        if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSearchResultState) {  //判断搜索页面状态，如果是true,
            } else {
                String call = "javascript:close_window()";
                mWebView.loadUrl(call);
                mSearchResultState = true;
                return true;         //如果是false让浏览器获取返回键状态，取消搜索提示
            }
            //首页各个Item:URL
            String homeItemUrl = AppCommonInfo.INDEX_URL + currentURL.substring(currentURL.length() - 1);
            //对当前页面进行判断，如果是首页，点击返回弹出退出提示
            if ((currentURL.equals(homeItemUrl))
                    || currentURL.equals(AppCommonInfo.WEBURL)) {
                Log.e(TAG, "onKeyDown: showExitDialog");
                showExitDialog();
                return true;
            } else if (mWebView.canGoBack()) {
                mWebView.loadUrl(endUrl);
                return true;
            } else {
                mWebView.loadUrl(endUrl);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.e(TAG, "onStop: ");
//        mWebView.stopLoading();
//        cleanApplicationData(MainActivity.this, "/data/data/com.guanghua.ln/cache/",
//                "/data/data/com.guanghua.ln/files/", "/data/data/com.guanghua.ln/database/");
//        /**
//         * 点击首页键，直接杀死进程，如果是在视频播放页，处于
//         * onStop,则不做任何处理
//         */
//        if (!AppCommonInfo.sInPlayVideo) {
//            System.exit(0);
//        }
//    }

    /**
     * 显示退出对话框
     */
    private void showExitDialog() {
        DialogFragment dialogFragment = CyDialogFragment.newInstance(1);
        dialogFragment.show(getFragmentManager(), "exitDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "onDestroy: ");
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        cleanApplicationData(MainActivity.this, "/data/data/com.guagnhua.ln/cache",
                "/data/data/com.guanghua.ln/files", "/data/data/com.guanghua.ln/database/");
        System.exit(0);
    }

}
