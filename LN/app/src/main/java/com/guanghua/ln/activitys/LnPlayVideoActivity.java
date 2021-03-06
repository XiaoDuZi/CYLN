package com.guanghua.ln.activitys;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.guanghua.ln.R;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.RecordIDBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnPlayUrlService;
import com.guanghua.ln.interfaces.LnRecordIdService;
import com.guanghua.ln.interfaces.PlayRecordService;
import com.guanghua.ln.utils.LnMD5Utils;
import com.guanghua.ln.utils.LnUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.guanghua.ln.common.AppCommonInfo.BASEURL;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.fileTypeList;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.mRecordID;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playIndex1;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playItemList;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playTitleList;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playTrackIdList;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playVodIdList;

public class LnPlayVideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "LnPlayVideoActivity";

    private static final int QUICK_ADD_PROGRESS = 5;    //快进
    private static final int QUICK_CUT_PROGRESS = 6;    //快退
    private static final int SHOW_CONTROL = 7;          //显示操作界面
    private static final int HIDE_CONTROL = 8;          //隐藏操作界面
    private static final int PLAY_TIME = 9;             //播放时间
    private static final int QUICK_INTERVAL_ITEM = 60;  //快进的时间间隔
    @BindView(R.id.videoView)
    VideoView mVideoView;
    @BindView(R.id.iv_video_bg)
    ImageView mIvVideoBg;
    @BindView(R.id.tv_dur_left)
    TextView mTvDurLeft;
    @BindView(R.id.video_player_progress)
    SeekBar mVideoPlayerProgress;
    @BindView(R.id.tv_dur_right)
    TextView mTvDurRight;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.vedio_player_pause)
    ImageButton mVedioPlayerPause;
    @BindView(R.id.rl_control)
    RelativeLayout mRlControl;
    @BindView(R.id.iv_quick_icon)
    ImageView mIvQuickIcon;
    @BindView(R.id.tv_quick_info)
    TextView mTvQuickInfo;
    @BindView(R.id.ll_play_quick)
    LinearLayout mLlPlayQuick;
    @BindView(R.id.lv_play)
    ListView mLvPlay;
    @BindView(R.id.rl_play_list)
    RelativeLayout mRlPlayList;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.ll_loading_layout)
    LinearLayout mLlLoadingLayout;
    @BindView(R.id.activity_RootView)
    RelativeLayout mActivityRootView;

    private long mTime;
    private String mRiddle;
    private String mPlatform;
    private String mContentId;
    private String mBeginTime;
    private String mEndTime;

    private boolean isPlaying = false;   //播放状态：暂停，播放
    private long mCurrentTime;           //当前播放时间
    private long mDuration;              //视频总时长
    private boolean startQuick = false;
    private long mSeekTime;              //快进快退时间
    private String mFileType;
    private LnPlayUrlBean mLnPlayUrlBean;

    private int mPlayVideoRecordID = mRecordID; //添加播放记录
    private String mTvName; //视频名称
    private String mUserName; //用户名：和keyNo相同
    private String mTrackID;  //
    private long mStopVideoTime;      //视频停止播放时间

    Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == QUICK_ADD_PROGRESS) { //快进
                uiHandler.sendEmptyMessage(QUICK_ADD_PROGRESS);
                setPlayInfo();
            } else if (message.what == QUICK_CUT_PROGRESS) { //快退
                uiHandler.sendEmptyMessage(QUICK_CUT_PROGRESS);
                setPlayInfo();
            } else if (message.what == SHOW_CONTROL) {
                mRlControl.setVisibility(View.VISIBLE);
            } else if (message.what == HIDE_CONTROL) {
                mRlControl.setVisibility(View.GONE);
            } else if (message.what == 11) {
                uiHandler.sendEmptyMessageDelayed(11, 1000);
                mStayTime++;
            } else if (message.what == 12) {
                uiHandler.sendEmptyMessageDelayed(12, 1000);
                mZTETime++;
                mTvDurLeft.setText(LnUtils.generateTimeInt(mZTETime));
                mVideoPlayerProgress.setProgress((int) ((mZTETime * 1000) / (mDuration / 1000)));
            } else if (message.what == 14) {
                mVideoView.seekTo((int) mSeekTime);
                mZTETime = (int) (mSeekTime / 1000);
                setPlayInfo();
            } else if (message.what == PLAY_TIME) {
                uiHandler.sendEmptyMessageDelayed(PLAY_TIME, 1000);
            }
            return false;
        }
    });


    private int mIsFree;
    private String mUserCode;

    private int mStayTime = 0;
    private int mZTETime = 0;
    private boolean mHasObtainRecordID = true;
    private boolean mBeginPlay = true;
    private boolean mHasObtainIsFree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_activity_play_video_ln);
        ButterKnife.bind(this);

        AppCommonInfo.sInPlayVideo = true;
        initDatas();   //获取播放信息
        getPlayUrl();

        showControl();

        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
    }


    /**
     * 获取播放RecordID
     *
     * @param playUrl
     */
    private void getRecordID(final Uri playUrl) {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.RECORDID_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnRecordIdService mLnRecordIdService = mRetrofit.create(LnRecordIdService.class);
        Call<RecordIDBean> mRecordIDBeanCall = mLnRecordIdService.getRecordID(mTrackID, mUserName);
        mRecordIDBeanCall.enqueue(new Callback<RecordIDBean>() {

            @Override
            public void onResponse(Call<RecordIDBean> call, Response<RecordIDBean> response) {
                Log.e(TAG, call + "onResponse: RecordID获取访问网络成功！" + response);
                RecordIDBean recordIDBean = new RecordIDBean();
                recordIDBean = response.body();
                try {
                    mPlayVideoRecordID = recordIDBean.getRecordId();
                    mIsFree = recordIDBean.getData().getIsfree();
                    mUserCode = recordIDBean.getData().getUserCode();
                    beginPlayVideo(playUrl);
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
     * 获取播放信息
     */
    private void initDatas() {

        mPlatform = UserLauncherBean.getInstance().getPlatform();   //获取平台
        if (UserLauncherBean.getInstance().getUserName() == null) { //判断用户名是否为空，如果为空赋默认值，便于添加播放记录
            mUserName = "9950000002581730";
        } else {
            mUserName = UserLauncherBean.getInstance().getUserName();   //获取用户名
        }
        if (playIndex1 >= playItemList.size()) {
            playIndex1 = 0;
        }
    }

    private void playNext() {
        Log.e(TAG, "playNext: ");
        addPlayRecord();     //添加播放记录
        if (Build.MANUFACTURER.equals("ZTE Corporation")) {
            mZTETime = 0;
        }
        playIndex1++;
        playVodByIndex();
    }

    /**
     * 播放暂停，快进控制
     */
    private void showControl() {
        uiHandler.removeMessages(HIDE_CONTROL);
        uiHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
        uiHandler.sendEmptyMessage(SHOW_CONTROL);
    }

    /**
     * 获取播放Url
     */
    private void getPlayUrl() {

        Log.e(TAG, "getPlayUrl: " + playIndex1);

        mTvName = playTitleList.get(playIndex1);  //获取视频名称
        mTrackID = playTrackIdList.get(playIndex1); //获取mTrackID
        mTvTitle.setText(mTvName);

        mFileType = fileTypeList.get(playIndex1);  //播放文件类型：1：视频：其他：音乐
//        if (TextUtils.equals(mFileType, "1")) { //判断是视频还是纯音乐
//            mIvVideoBg.setImageResource(R.mipmap.home_bg_no_use);
//        } else { //音乐
//            mIvVideoBg.setImageResource(R.mipmap.play_music_bg);
//            mVideoView.setVisibility(View.INVISIBLE);
//        }
        mTime = System.currentTimeMillis();                                     //获取时间戳
        mRiddle = LnMD5Utils.MD5(mTime + AppCommonInfo.PLAY_KEY);           //加密串加密串（时间戳+key的md5值），

        // key值固定写为besto
        mContentId = playVodIdList.get(playIndex1);    //获取视频ID

        Retrofit retrofit = new Retrofit.Builder()                          //使用Retrofit网络框架进行访问网络
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnPlayUrlService lnPlayUrlService = retrofit.create(LnPlayUrlService.class);
        Call<LnPlayUrlBean> call = lnPlayUrlService.getPlayUrlInfo(AppCommonInfo.Type, mTime,
                mRiddle, mPlatform, AppCommonInfo.SpId, mContentId, mBeginTime, mEndTime);
        call.enqueue(new Callback<LnPlayUrlBean>() {
            @Override
            public void onResponse(Call<LnPlayUrlBean> call, Response<LnPlayUrlBean> response) {
                Log.e(TAG, "onResponse: 请求视频url网络成功" + response.toString());
                mLnPlayUrlBean = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run：请求视频url网络" + mLnPlayUrlBean.getPlayUrl().toString());
                        if (mLnPlayUrlBean == null)
                            return;
                        getPlatform(mLnPlayUrlBean);       //播放视频
                    }
                });
            }

            @Override
            public void onFailure(Call<LnPlayUrlBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 请求视频链接失败！");
                Toast.makeText(LnPlayVideoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void getPlatform(LnPlayUrlBean lnPlayUrlBean) {
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
                if (mHasObtainRecordID) {  //判断是否已经获取到RecordID,如果已经获取到就不重复获取RecordID
                    mHasObtainRecordID = false;
                    beginPlayVideo(playUrl);
                } else {
                    getRecordID(playUrl);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void beginPlayVideo(Uri playUrl) {

        uiHandler.removeMessages(11);
        mStayTime = 0;
        uiHandler.sendEmptyMessage(11);

        Log.e(TAG, "beginPlayVideo: " + mIsFree + ":" + mUserCode);

        if (mHasObtainIsFree) {
            mVideoView.setVideoPath(playUrl.toString());
            mHasObtainIsFree = false;
        } else {
            if (mIsFree == 0 && mUserCode.equals("201")) {//免费视频，和未定够状态提示订购
                AlertDialog.Builder builder = new AlertDialog.Builder(LnPlayVideoActivity.this);
                builder.setMessage("您尚未订购该产品，请订购后，继续收看该节目！")
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return;
            } else if (mIsFree == 1 || mUserCode.equals("200")) {//如果是白名单，直接播放
                //正式地址
                mVideoView.setVideoPath(playUrl.toString());
            }
        }

        mVideoView.setVideoPath(playUrl.toString());
        Log.e(TAG, "getPlatform: " + mPlatform);
        mVideoView.setOnPreparedListener(this);
        mVideoView.requestFocus();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showControl();
        Log.e(TAG, "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {   //OK,确认键暂停播放
            setPauseOrPlay();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {           //快进
            mRlPlayList.setVisibility(View.GONE);
            mVedioPlayerPause.setVisibility(View.GONE);
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                mSeekTime = mZTETime * 1000 + 3000;
                uiHandler.sendEmptyMessage(14);
            } else {
                mSeekTime = (mCurrentTime + 3000);    //快进
                mVideoView.seekTo((int) mSeekTime);
                startQuickThread(QUICK_ADD_PROGRESS);
            }
            mIvQuickIcon.setImageResource(R.mipmap.quick_forward);
            mLlPlayQuick.setVisibility(View.VISIBLE);
            mVideoView.start();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {           //快退
            mVideoView.pause();
            mRlPlayList.setVisibility(View.GONE);      //隐藏视频列表
            mVedioPlayerPause.setVisibility(View.GONE);//隐藏视频暂停图标
            mIvQuickIcon.setImageResource(R.mipmap.quick_back); //设置快退图标
            mLlPlayQuick.setVisibility(View.VISIBLE);   //显示快进快退布局
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                mSeekTime = mZTETime * 1000 - 3000;
                if (mSeekTime <= 0) {
                    mSeekTime = 0;
                }
                uiHandler.sendEmptyMessage(14);
            } else {
                mSeekTime = (mCurrentTime - 3000);         //设置快退时间
                mVideoView.seekTo((int) mSeekTime);
                startQuickThread(QUICK_CUT_PROGRESS);
            }
            mVideoView.start();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mRlPlayList.getVisibility() == View.VISIBLE) {
                mRlPlayList.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            uiHandler.removeMessages(QUICK_CUT_PROGRESS);
//            uiHandler.removeMessages(QUICK_ADD_PROGRESS);
            mLlPlayQuick.setVisibility(View.GONE);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mRlPlayList.getVisibility() == View.GONE) {
                showPlayList();
                return true;
            }
            mVideoView.setFocusable(false);    //Video取消选中后，ListView播放列表才会获得焦点，
            mLvPlay.setFocusable(true);
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 显示播放列表
     */
    private void showPlayList() {
        mRlPlayList.setVisibility(View.VISIBLE);
        mLvPlay.setAdapter(new ArrayAdapter<String>(this, R.layout.ln_item_play_list, playTitleList));
        mLvPlay.setSelection(playIndex1);
        mLvPlay.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.ACTION_DOWN == event.getAction() && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (mVideoView.isPlaying()) {
                        addPlayRecord();
                    } else {
                        mCurrentTime = 0;
                        addPlayRecord();
                    }

                }
                return false;
            }
        });
        mLvPlay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStopVideoTime = System.currentTimeMillis();
                playIndex1 = i;
                if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                    mZTETime = 0;
                }
                playVodByIndex();  //播放选中的 视频
                mRlPlayList.setVisibility(View.GONE);
            }
        });
    }

    private void playVodByIndex() {
        if (playIndex1 >= playVodIdList.size()) {
            finish();
            return;
        }
        isPlaying = false;
        mLlLoadingLayout.setVisibility(View.VISIBLE);
        mIvVideoBg.setVisibility(View.VISIBLE);
        uiHandler.removeMessages(PLAY_TIME);
        mTvDurLeft.setText("00:00");
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        getPlayUrl();
        showControl();
    }

    /**
     * 添加播放记录
     */
    private void addPlayRecord() {
        if (Build.MANUFACTURER.equals("ZTE Corporation")) {
            mCurrentTime = mZTETime;
        }

        Retrofit addPlayRecordRetrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.PLAY_RECORD_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlayRecordService playRecordService = addPlayRecordRetrofit.create(PlayRecordService.class);
        Call<String> playRecordCall = playRecordService.getPlayRecord(mUserName, mPlayVideoRecordID, mTrackID,
                mContentId, mTvName, mCurrentTime / 1000 + "", mStayTime + "");

        playRecordCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "onResponse: 添加播放记录成功！" + mTvName + response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: 添加播放记录失败！");
            }
        });
    }


    private void startQuickThread(int what) {
        if (startQuick) {
            return;
        }
        Log.d("quickCount", "开始计时线程" + what);
        startQuick = true;
        uiHandler.sendEmptyMessage(what);
    }

    /**
     * 设置播放和暂停
     */
    private void setPauseOrPlay() {
        if (mVideoView == null) {
            return;
        }
        if (isPlaying) {
            mVedioPlayerPause.setImageResource(R.drawable.player_play);
            mVedioPlayerPause.setVisibility(View.VISIBLE);
            mVideoView.start();
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                uiHandler.sendEmptyMessage(12);
            }
            isPlaying = false;
            uiHandler.removeMessages(PLAY_TIME);
        } else {
            mVedioPlayerPause.setImageResource(R.drawable.player_pause);
            mVedioPlayerPause.setVisibility(View.VISIBLE);
            mVideoView.pause();
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                uiHandler.removeMessages(12);
            }
            isPlaying = true;
            uiHandler.sendEmptyMessage(PLAY_TIME);
        }
    }

    /**
     * 快进，快退，时间显示
     */
    private void setPlayInfo() {
        if (mSeekTime < 0 || mSeekTime > mDuration) {
            return;
        }
        String seekDurationTime = LnUtils.generateTime(mSeekTime) + "/" + LnUtils.generateTime(mDuration);
        mTvQuickInfo.setText(seekDurationTime);
    }

    /**
     * 完成播放自动播放下一个节目
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        mIvVideoBg.setVisibility(View.VISIBLE);
        mTvDurLeft.setText("00:00");
        mTvDurRight.setText("00:00");
        playNext();          //播放完成，自动播放下一个节目
    }

    /**
     * 获取视频总长度，和当前播放时间
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "onPrepared: " + "开始播放视频");
        mIvVideoBg.setVisibility(View.GONE);
        mLlLoadingLayout.setVisibility(View.GONE);
        if (mp.isPlaying())
            mp.reset();
        mp.start();
        if (mBeginPlay) {
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                uiHandler.sendEmptyMessage(12);
            }
            mBeginPlay = false;
        }

        mDuration = mVideoView.getDuration();               //获取视频总时间
        mTvDurRight.setText(LnUtils.generateTime(mDuration));//显示视频总时长
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mProgressBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                // 获得当前播放时间和当前视频的长度
//                int currentPosition =mVideoView.getCurrentPosition();
//                int duration = mVideoView.getDuration();
//                int time = ((currentPosition * 100) / duration);
//                Log.e(TAG, "onBufferingUpdate: "+time+":::"+duration+"  current"+currentPosition);
//                Log.e(TAG, "onBufferingUpdate: "+LnUtils.generateTime(mCurrentTime));

                if (!Build.MANUFACTURER.equals("ZTE Corporation")) {
                    mCurrentTime = mVideoView.getCurrentPosition();    //当前播放时间获取
                    mVideoView.getDuration();
                    mVideoPlayerProgress.setProgress((int) ((mCurrentTime * 1000) / mDuration)); //进度条
                    mTvDurLeft.setText(LnUtils.generateTime(mCurrentTime));
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStopVideoTime = System.currentTimeMillis();
        addPlayRecord();     //添加播放记录
        System.gc();         //退出回收系统垃圾
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: mp:" + mp.toString() + "what:" + what + "extra:" + extra);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("无法播放此视频！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        return true;
    }
}
