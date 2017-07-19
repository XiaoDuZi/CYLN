package com.guanghua.ln.activitys;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.guanghua.ln.R;
import com.guanghua.ln.common.AppCommonInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

//    @BindView(R.id.wv_order)
//    WebView mWvOrder;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.bt_order)
    Button mBtOrder;
    @BindView(R.id.bt_play)
    Button mBtPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        AppCommonInfo.sInPlayVideo = true;

//        mWvOrder.loadUrl("");
////        mWvOrder.addJavascriptInterface(new OrderJsAndroidInteractive(), "android");
//        mWvOrder.addJavascriptInterface(new OrderJsAndroidInteractive(), "android");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + ":" + resultCode);
        switch (requestCode) {
            case 0:
                Log.e(TAG, "onActivityResult: " + resultCode + ":");
                break;
            default:
                break;
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
        intent.putExtra("videoname", "变形金刚5");
        intent.putExtra("desc","晚上一起去看变形金刚吧，亲！");
//        intent.putExtra("desc",programid);
//        intent.putExtra("contentid","MOV57ce95781170aa34867effa8");
        intent.putExtra("contentid", "PRO56b454431170126b36ef96fc");
        intent.putExtra("spid", "YPPL");
        intent.putExtra("contenttype", "1");
        intent.putExtras(intent);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCommonInfo.sInPlayVideo = false;
    }

    @OnClick({R.id.bt_order, R.id.bt_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_order:
                goToOrderActivity();

                break;
            case R.id.bt_play:
                String playListJsonString = getIntent().getStringExtra("playListJsonString");//获取播放信息
                int playIndex = getIntent().getIntExtra("playIndex", 0);            //获取播放位置索引
                Intent intent = new Intent(OrderActivity.this, LnPlayVideoActivity.class);
                intent.putExtra("playIndex", playIndex);
                intent.putExtra("playListJsonString", playListJsonString);
                Log.e(TAG, "startPlayVideo: " + playIndex + ":::::" + playListJsonString);
//        mActivity.startActivityForResult(intent, PLAY_VIDEO);
                startActivity(intent);
                break;
        }
    }

    private final class OrderJsAndroidInteractive {
        @JavascriptInterface
        public void jumpOrderActivity() {
            goToOrderActivity();
        }
    }
}
