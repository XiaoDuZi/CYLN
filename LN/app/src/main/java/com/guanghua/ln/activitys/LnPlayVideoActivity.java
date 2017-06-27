package com.guanghua.ln.activitys;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanghua.ln.bean.LnBeanPlayItem;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.RecordIDBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnPlayUrlService;
import com.guanghua.ln.interfaces.LnRecordIdService;
import com.guanghua.ln.interfaces.PlayRecordService;
import com.guanghua.ln.utils.LnMD5Utils;
import com.guanghua.ln.utils.LnUtils;
import com.guanghua.ln.views.LnVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LnPlayVideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "LnPlayVideoActivity";

    private static final int QUICK_ADD_PROGRESS = 5;    //快进
    private static final int QUICK_CUT_PROGRESS = 6;    //快退
    private static final int SHOW_CONTROL = 7;          //显示操作界面
    private static final int HIDE_CONTROL = 8;          //隐藏操作界面
    private static final int PLAY_TIME = 9;             //播放时间
    private static final int QUICK_INTERVAL_ITEM = 60;  //快进的时间间隔


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
    @BindView(R.id.activity_RootView)
    RelativeLayout mActivityRootView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.ll_loading_layout)
    LinearLayout mLlLoadingLayout;
    @BindView(R.id.videoView)
    LnVideoView mVideoView;

    private long mTime;
    private String mRiddle;
    //    private String mPlatform = "GD";
    private String mPlatform;
    private String mSpId = "YPPL";
    //    private String mContentId= "MOV57ce95781170aa34867effa8";
    private String mContentId;
    private String mBeginTime;
    private String mEndTime;

    private boolean isPlaying = false;   //播放状态：暂停，播放
    private long mCurrentTime;           //当前播放时间
    private long mDuration;              //视频总时长
    private boolean startQuick = false;
    private long mSeekTime;              //快进快退时间
    private int playIndex = 0;           //播放索引

    private ArrayList<String> playTitleList = new ArrayList();
    private ArrayList<String> playVodIdList = new ArrayList();
    private ArrayList<String> playTrackIdList = new ArrayList();
    private ArrayList<String> fileTypeList = new ArrayList();
    private ArrayList<Integer> pointList = new ArrayList();
    private String mFileType;
    private LnPlayUrlBean mLnPlayUrlBean;

    private int mRecordID; //添加播放记录
    private String mTvName; //视频名称
    private String mUserName; //用户名：和keyNo相同
    private String mTrackID;  //
    private long mComeInActivityTime;  //页面显示时时间
    private long mOutActivityTime;      //页面销毁时时间

    Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == QUICK_ADD_PROGRESS) { //快进
                uiHandler.sendEmptyMessageDelayed(QUICK_ADD_PROGRESS, QUICK_INTERVAL_ITEM);
                setPlayInfo();
            } else if (message.what == QUICK_CUT_PROGRESS) { //快退
                uiHandler.sendEmptyMessageDelayed(QUICK_CUT_PROGRESS, QUICK_INTERVAL_ITEM);
                setPlayInfo();
            } else if (message.what == SHOW_CONTROL) {
                mRlControl.setVisibility(View.VISIBLE);
            } else if (message.what == HIDE_CONTROL) {
                mRlControl.setVisibility(View.GONE);
            } else if (message.what == PLAY_TIME) {
                uiHandler.sendEmptyMessageDelayed(PLAY_TIME, 1000);
//                vodPayingTime++;
//                if (vodPayingTime * 1000 > vodDur) {
////                    playNext();//// TODO: 17/1/18 要去掉
//                    return false;
//                }
//                mTxtPlayTime.setText(StrTool.generateTime(vodPayingTime * 1000));
//                video_player_progress.setProgress((int) (vodPayingTime * 1000 * video_player_progress.getMax() / vodDur));
//                if (freeTime != -1 && vodPayingTime > freeTime) {//免费播放时长结束，返回webview界面提示需要订购
//                    Gson gson = new Gson();
//                    String cmboId = "";
//                    if (authBean != null && authBean.getData() != null) {
//                        cmboId = gson.toJson(authBean.getData().getCmboIds());
//                    }
//                    Intent intent = new Intent();
//                    intent.putExtra("cmboIdsArrayStr", cmboId);
//                    intent.putExtra("currentIndex", playIndex);
//                    intent.putExtra("currentPoint", vodPayingTime);
//                    setResult(RESULT_OK, intent);
//                    isBuyBack = true;
//                    finish(); // TODO: 17/1/18
//                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_activity_play_video_ln);
        ButterKnife.bind(this);
//        getRecordID();//
        initDatas();   //获取播放信息
        getPlayUrl();

        showControl();

        mVideoView.setOnCompletionListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mComeInActivityTime = System.currentTimeMillis();  //页面显示时时间
    }

    /**
     * 获取播放RecordID
     */
    private void getRecordID(){
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.RECORDID_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnRecordIdService mLnRecordIdService= mRetrofit.create(LnRecordIdService.class);
        Call<RecordIDBean> mRecordIDBeanCall=mLnRecordIdService.getRecordID(mTrackID,mUserName);
        mRecordIDBeanCall.enqueue(new Callback<RecordIDBean>() {

            @Override
            public void onResponse(Call<RecordIDBean> call, Response<RecordIDBean> response) {
                Log.e(TAG, "onResponse: 访问网络成功！");
                RecordIDBean recordIDBean=new RecordIDBean();
                recordIDBean=response.body();
                mRecordID = recordIDBean.getRecordId();
                Log.e(TAG, "onResponse: "+recordIDBean.toString());
            }

            @Override
            public void onFailure(Call<RecordIDBean> call, Throwable t) {
                Log.e(TAG, "onFailure: "+"请求网络失败");
            }
        });
    }

    /**
     * 获取播放信息
     */
    private void initDatas() {

        mPlatform = UserLauncherBean.getInstance().getPlatform();   //获取平台
        mUserName = UserLauncherBean.getInstance().getUserName();   //获取用户名

        String playListJsonString = getIntent().getStringExtra("playListJsonString");//获取播放信息
        playIndex = getIntent().getIntExtra("playIndex", 0);            //获取播放位置索引
        List<LnBeanPlayItem> playItemList = new ArrayList<>();
        Gson gson = new Gson();
        playItemList = gson.fromJson(playListJsonString, new TypeToken<List<LnBeanPlayItem>>() {
        }.getType());

        if (playItemList.isEmpty()) {
            return;
        }
        for (int i = 0; i < playItemList.size(); i++) {
            LnBeanPlayItem playItem = playItemList.get(i);
            playTitleList.add(playItem.getName());
            playVodIdList.add(playItem.getVodId());
            fileTypeList.add(playItem.getFileType());
            pointList.add(playItem.getInitPoint());
            playTrackIdList.add(playItem.getTrackId() + "");
        }
        if (playIndex >= playItemList.size()) {
            playIndex = 0;
        }
    }

    private void playNext() {
        playIndex++;
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

        getRecordID();  //获取添加播放记录鉴权

        mTvName = playTitleList.get(playIndex);
        mTrackID = playTrackIdList.get(playIndex);

        mTvTitle.setText(mTvName);

        mFileType = fileTypeList.get(playIndex);
//        if (TextUtils.equals(mFileType, "1")) { //判断是视频还是纯音乐
//            mIvVideoBg.setImageResource(R.mipmap.home_bg);
//        } else { //音乐
//            mIvVideoBg.setImageResource(R.mipmap.play_music_bg);
//            mVideoView.setVisibility(View.INVISIBLE);
//        }
        mTime = System.currentTimeMillis();                                     //获取时间戳
        mRiddle = LnMD5Utils.MD5(System.currentTimeMillis() + "besto");           //加密串加密串（时间戳+key的md5值），

        // key值固定写为besto
        mContentId = playVodIdList.get(playIndex);    //获取视频ID
//        mContentId = "PRO56b454431170126b36ef96fc";
//        Log.e(TAG, "getPlayUrl:mRiddle" + mRiddle);
//        Log.e(TAG, "getPlayUrl: " + mPlatform);
//
//        String url = "http://59.46.18.25:99/spplayurl/returnPlayUrl.do?" +
//                "type=4&time=" + mTime + "&riddle=" + mRiddle + "&platform=" + mPlatform +
//                "&spid=YPPL&contentid=" + mContentId + "&begintime=&endtime=";
//
//        Log.e(TAG, "getPlayUrl: URL" +mRecordID+ url);
        Retrofit retrofit = new Retrofit.Builder()                          //使用Retrofit网络框架进行访问网络
                .baseUrl(AppCommonInfo.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnPlayUrlService lnPlayUrlService = retrofit.create(LnPlayUrlService.class);

        Call<LnPlayUrlBean> call = lnPlayUrlService.getPlayUrlInfo(AppCommonInfo.Type, mTime,
                mRiddle, AppCommonInfo.Platform, AppCommonInfo.SpId, mContentId, mBeginTime, mEndTime);
        call.enqueue(new Callback<LnPlayUrlBean>() {
            @Override
            public void onResponse(Call<LnPlayUrlBean> call, Response<LnPlayUrlBean> response) {
                Toast.makeText(LnPlayVideoActivity.this, "请求成功!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onResponse: " + "请求网络成功");
                Log.e(TAG, "onResponse: " + mContentId + ":" + mPlatform);
                Log.e(TAG, "onResponse: " + response.body().toString());
                mLnPlayUrlBean = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: " + mLnPlayUrlBean.getPlayUrl());
                        playVideo(mLnPlayUrlBean);       //播放视频
                    }
                });
            }

            @Override
            public void onFailure(Call<LnPlayUrlBean> call, Throwable t) {
                Toast.makeText(LnPlayVideoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void playVideo(LnPlayUrlBean lnPlayUrlBean) {
        Log.e(TAG, "playVideo: ****************************************");
        if (lnPlayUrlBean.getPlayUrl() == null)
            return;
        try {
            Log.e(TAG, "playVideo: " + lnPlayUrlBean.getPlayUrl());
            Uri playUrl = Uri.parse(lnPlayUrlBean.getPlayUrl());
            Log.e(TAG, "onResponse: " + playUrl.toString());
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

//        getRecordID();  //获取添加播放记录鉴权

        Log.e(TAG, "playVideo: " + mPlatform);
        //正式地址
        mVideoView.setVideoPath(playUrl.toString());
        //测试视频地址
//        mVideoView.setVideoPath(path);
        mVideoView.setOnPreparedListener(this);
        mVideoView.requestFocus();
//        if (TextUtils.equals(mFileType, "1")) { //视频播放
//            mIvVideoBg.setVisibility(View.GONE);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showControl();
        Log.e(TAG, "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {   //OK,确认键暂停播放
            setPauseOrPlay();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {           //快进
//            if (mRlPlayList.getVisibility() == View.VISIBLE) {
//                mRlPlayList.setVisibility(View.GONE);
//            }
            mRlPlayList.setVisibility(View.GONE);
            mVedioPlayerPause.setVisibility(View.GONE);
            mSeekTime = (mCurrentTime + 3000);    //快进
            mVideoView.seekTo((int) mSeekTime);
            mIvQuickIcon.setImageResource(R.mipmap.quick_forward);
            mLlPlayQuick.setVisibility(View.VISIBLE);
            startQuickThread(QUICK_ADD_PROGRESS);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {           //快退
//            if (mRlPlayList.getVisibility() == View.VISIBLE) {
//                mRlPlayList.setVisibility(View.GONE);
//            }
            mVideoView.pause();
            mRlPlayList.setVisibility(View.GONE);
            mVedioPlayerPause.setVisibility(View.GONE);
            mSeekTime = (mCurrentTime - 3000);
            mVideoView.seekTo((int) mSeekTime);
            mIvQuickIcon.setImageResource(R.mipmap.quick_back);
            mLlPlayQuick.setVisibility(View.VISIBLE);
            startQuickThread(QUICK_CUT_PROGRESS);
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
            Log.e(TAG, "onKeyUp: ");
            if (mRlPlayList.getVisibility() == View.GONE) {
                showPlayList();
                Log.e(TAG, "onKeyUp: **********");
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
        Log.e(TAG, "showPlayList: ");
        mRlPlayList.setVisibility(View.VISIBLE);

        mLvPlay.setAdapter(new ArrayAdapter<String>(this, R.layout.ln_item_play_list, playTitleList));

        mLvPlay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                addPlayRecord(); //选择视频后添加到历史播放记录
                playIndex = i;
                playVodByIndex();  //播放选中的 视频
                mRlPlayList.setVisibility(View.GONE);
            }
        });
    }

    private void playVodByIndex() {
        if (playIndex >= playVodIdList.size()) {
            finish();
            return;
        }
        isPlaying = false;
//        mCurrentTime = 0;
//        mVideoPlayerProgress.setProgress(0);
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

        long StayTime=mOutActivityTime-mComeInActivityTime;   //获取页面停留时间

        Log.e(TAG, "addPlayRecord: "+mUserName+":"+mRecordID+":"+mTrackID+":"+mContentId+":"+
        mTvName+":"+StayTime);

        Retrofit addPlayRecordRetrofit=new Retrofit.Builder()
                .baseUrl(AppCommonInfo.PLAY_RECORD_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlayRecordService playRecordService=addPlayRecordRetrofit.create(PlayRecordService.class);
        Call<String> playRecordCall=playRecordService.getPlayRecord(mUserName,mRecordID,mTrackID,
                mContentId,mTvName,mCurrentTime+"",StayTime+"");
        playRecordCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "onResponse: 添加播放记录成功！"+mTvName);
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
            mVedioPlayerPause.setImageResource(R.mipmap.player_play);
            mVedioPlayerPause.setVisibility(View.VISIBLE);
            mVideoView.start();
            isPlaying = false;
            uiHandler.removeMessages(PLAY_TIME);
        } else {
            mVedioPlayerPause.setImageResource(R.mipmap.player_pause);
            mVedioPlayerPause.setVisibility(View.VISIBLE);
            mVideoView.pause();
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
        addPlayRecord();     //播放完成，添加播放记录
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
        mDuration = mVideoView.getDuration();               //获取视频总时间
        mTvDurRight.setText(LnUtils.generateTime(mDuration));
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
                mCurrentTime = mVideoView.getCurrentPosition();                     //当前播放时间获取
                mVideoPlayerProgress.setProgress((int) ((mCurrentTime * 1000) / mDuration)); //进度条
                mTvDurLeft.setText(LnUtils.generateTime(mCurrentTime));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面退出时时间
        mOutActivityTime = System.currentTimeMillis();
        addPlayRecord();
        System.gc();
    }

}
