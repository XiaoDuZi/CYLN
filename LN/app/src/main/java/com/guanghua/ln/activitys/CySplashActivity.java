package com.guanghua.ln.activitys;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.guanghua.ln.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CySplashActivity extends AppCompatActivity {

    private static final String TAG = "CySplashActivity";

    Handler handler = new Handler();
    @BindView(R.id.tv_vision_code)
    TextView mTvVisionCode;
    @BindView(R.id.bg_splash_bg)
    SimpleDraweeView mBgSplashBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        final String mWebUrl = getIntent().getStringExtra("CYURL");
        Log.e(TAG, "onCreate: " + mWebUrl);
        Uri uri = Uri.parse("http://59.46.18.18/ott/app/splash_bg.jpg");
        mBgSplashBg.setImageURI(uri);

        mTvVisionCode.setText(getVersion());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CySplashActivity.this, MainActivity.class);
                intent.putExtra("CYURL", mWebUrl);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.app_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.app_name);
        }
    }
}
