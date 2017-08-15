package com.guanghua.ln.activitys;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.guanghua.ln.R;
import com.guanghua.ln.adapters.PlayListAdapter;
import com.guanghua.ln.adapters.PlayListTitleAdapter;
import com.guanghua.ln.bean.LnPlayUrlBean;
import com.guanghua.ln.bean.PlayListBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnPlayUrlService;
import com.guanghua.ln.interfaces.PlayListService;
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

import static com.guanghua.ln.common.AppCommonInfo.BASEURL;
import static com.guanghua.ln.utils.LnJSAndroidInteractive.playVodIdList;

public class PlayListActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, AdapterView.OnItemSelectedListener, MediaPlayer.OnCompletionListener, AdapterView.OnItemClickListener, View.OnKeyListener {

    private static final String TAG = "PlayListActivity";

    @BindView(R.id.tv_list_page)
    TextView mTvListPage;
    @BindView(R.id.play_list_video)
    LnVideoView mPlayListVideo;
    @BindView(R.id.sb_play_list_seek_bar)
    SeekBar mSbPlayListSeekBar;
    @BindView(R.id.tv_play_list_left_time)
    TextView mTvPlayListLeftTime;
    @BindView(R.id.tv_play_list_right_time)
    TextView mTvPlayListRightTime;
    @BindView(R.id.lv_play_list)
    ListView mListView;
    @BindView(R.id.tv_play_list)
    TextView mTvPlayList;
    @BindView(R.id.iv_play_list_video_up_info)
    ImageView mIvPlayListVideoUpInfo;
    @BindView(R.id.iv_play_list_video_down_info)
    ImageView mIvPlayListVideoDownInfo;
    @BindView(R.id.ll_play_list)
    LinearLayout mLlPlayList;
    @BindView(R.id.ll_play_video_content)
    LinearLayout mLlPlayVideo;
    @BindView(R.id.rl_play_video)
    RelativeLayout mRlPlayVideo;
    @BindView(R.id.tv_play_list_dur_left)
    TextView mTvDurLeft;
    @BindView(R.id.sb_play_list_video_player_progress)
    SeekBar mVideoPlayerProgress;
    @BindView(R.id.tv_play_list_dur_right)
    TextView mTvDurRight;
    @BindView(R.id.tv_play_list_title)
    TextView mTvTitle;
    @BindView(R.id.play_list_video_player_pause)
    ImageView mVideoPlayerPause;
    @BindView(R.id.rl_control)
    RelativeLayout mRlControl;
    @BindView(R.id.iv_play_list_quick_icon)
    ImageView mIvQuickIcon;
    @BindView(R.id.tv_play_list_quick_info)
    TextView mTvQuickInfo;
    @BindView(R.id.ll_play_list_play_quick)
    LinearLayout mLlPlayQuick;
    @BindView(R.id.lv_play_list_play)
    ListView mLvPlay;
    @BindView(R.id.ll_play_list_seek_bar)
    LinearLayout mLlPlayListSeekBar;

    private static final int QUICK_ADD_PROGRESS = 5;    //快进
    private static final int QUICK_CUT_PROGRESS = 6;    //快退
    private static final int SHOW_CONTROL = 7;          //显示操作界面
    private static final int HIDE_CONTROL = 8;          //隐藏操作界面
    private static final int PLAY_TIME = 9;             //播放时间

    private List<PlayListBean.DataBean.TrackListBean> mTrackList = new ArrayList<>();
    private List<String> mTrackName = new ArrayList<>();
    private List<String> mVodId = new ArrayList<>();
    private LnPlayUrlBean mLnPlayUrlBean;
    private String mPlatform;
    private PlayListBean.DataBean.TrackListBean mTrackListBean;
    private long mDuration;
    private long mCurrentTime;
    private boolean mFullVideo = false;  //全屏状态：false:
    public static boolean isPlaying = false;   //播放状态：暂停，播放;设置为public为在adapter中调用使用
    private long mSeekTime;              //快进快退时间
    private int mStayTime = 0;
    private int mZTETime = 0;
    private boolean startQuick = false;

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
                if (mFullVideo){
                    mRlControl.setVisibility(View.VISIBLE);
                    mRlControl.clearFocus();
                }
            } else if (message.what == HIDE_CONTROL) {
                if (mVideoPlayerPause.getVisibility()==View.VISIBLE&&isPlaying==false){
                    mVideoPlayerPause.setVisibility(View.GONE);
                }
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
                mPlayListVideo.seekTo((int) mSeekTime);
                mZTETime = (int) (mSeekTime / 1000);
                setPlayInfo();
            } else if (message.what == PLAY_TIME) {
                uiHandler.sendEmptyMessageDelayed(PLAY_TIME, 1000);
            }else if (message.what==15){
                showPlayTitleList();
            }
            return false;
        }
    });
    private int mListPosition;
    private String mAlbumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_play_list);
        ButterKnife.bind(this);
        mPlatform = UserLauncherBean.getInstance().getPlatform();//获取平台
        initPlayDatas();
        mListView.setOnItemSelectedListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnKeyListener(this);
        mListView.setFocusable(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            setPauseOrPlay();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_0) {
            fullVideo();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLvPlay.getVisibility() == View.VISIBLE) {
                mLvPlay.setVisibility(View.GONE);
                return true;
            }
            if (stopFullVideo()) return true;
        }
//        else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            if (mFullVideo) {
//                showPlayTitleList();
////                return false;
//            }
//            return false;
//        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            setFast_Forward();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            setRewind();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mLlPlayQuick.setVisibility(View.GONE);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mFullVideo) {
                if (mLvPlay.getVisibility() == View.INVISIBLE||mLvPlay.getVisibility()==View.GONE) {
//                    uiHandler.sendEmptyMessage(15);
                    showPlayTitleList();
                    return true;
                }
                mPlayListVideo.setFocusable(false);    //Video取消选中后，ListView播放列表才会获得焦点，
                mVideoPlayerPause.setFocusable(false);
                mLvPlay.setFocusable(true);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 视频全屏时，按上下显示视频列表
     */
    private void showPlayTitleList() {
        mLvPlay.setVisibility(View.VISIBLE);
        mLvPlay.setFocusable(true);
        PlayListTitleAdapter titleAdapter=new PlayListTitleAdapter(mTrackList,PlayListActivity.this);
        mLvPlay.setAdapter(titleAdapter);
        mLvPlay.setSelection(mListPosition);
        mLvPlay.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.ACTION_DOWN == event.getAction() && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (mPlayListVideo.isPlaying()) {
//                        addPlayRecord();
                    } else {
                        mCurrentTime = 0;
//                        addPlayRecord();
                    }

                }
                return false;
            }
        });
        mLvPlay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mStopVideoTime = System.currentTimeMillis();
                mListPosition = i;
                Log.e(TAG, "onItemClick: "+i);
                if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                    mZTETime = 0;
                }
                playVodByIndex();  //播放选中的 视频
                mLvPlay.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 快进
     */
    private void setFast_Forward() {
        mLvPlay.setVisibility(View.GONE);
        mVideoPlayerPause.setVisibility(View.GONE);
        if (Build.MANUFACTURER.equals("ZTE Corporation")) {
            mSeekTime = mZTETime * 1000 + 3000;
            uiHandler.sendEmptyMessage(14);
        } else {
            mSeekTime = (mCurrentTime + 3000);    //快进
            mPlayListVideo.seekTo((int) mSeekTime);
            startQuickThread(QUICK_ADD_PROGRESS);
        }
        mIvQuickIcon.setImageResource(R.mipmap.quick_forward);
        mLlPlayQuick.setVisibility(View.VISIBLE);
        mPlayListVideo.start();
    }

    /**
     * 快退
     */
    private void setRewind() {
        mPlayListVideo.pause();
        mLvPlay.setVisibility(View.GONE);      //隐藏视频列表
        mVideoPlayerPause.setVisibility(View.GONE);//隐藏视频暂停图标
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
            mPlayListVideo.seekTo((int) mSeekTime);
            startQuickThread(QUICK_CUT_PROGRESS);
        }
        mPlayListVideo.start();
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
        if (isPlaying) {
            mVideoPlayerPause.setImageResource(R.drawable.player_play);
            mVideoPlayerPause.setVisibility(View.VISIBLE);
            mPlayListVideo.start();
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                uiHandler.sendEmptyMessage(12);
            }
            isPlaying = false;
            uiHandler.sendEmptyMessage(SHOW_CONTROL);
            uiHandler.sendEmptyMessageDelayed(HIDE_CONTROL,3000);
//                uiHandler.removeMessages(PLAY_TIME);
        } else {
            mVideoPlayerPause.setImageResource(R.drawable.player_pause);
            mVideoPlayerPause.setVisibility(View.VISIBLE);
            mPlayListVideo.pause();
            if (Build.MANUFACTURER.equals("ZTE Corporation")) {
                uiHandler.removeMessages(12);
            }
            isPlaying = true;
            uiHandler.sendEmptyMessage(SHOW_CONTROL);
//            uiHandler.sendEmptyMessageDelayed(HIDE_CONTROL,3000);
//                uiHandler.sendEmptyMessage(PLAY_TIME);
        }
        Log.e(TAG, "setPauseOrPlay: " + mFullVideo + ":" + isPlaying);
//        if (!mFullVideo) {
//            if (isPlaying) {
//                mPlayListVideo.start();
//                isPlaying = false;
//            } else if (!isPlaying) {
//                mPlayListVideo.pause();
//                isPlaying = true;
//            }
//        } else if (mFullVideo) {
////            showControl();
//            if (isPlaying) {
//                mVideoPlayerPause.setImageResource(R.mipmap.player_play);
//                mVideoPlayerPause.setVisibility(View.VISIBLE);
//                mPlayListVideo.start();
//                if (Build.MANUFACTURER.equals("ZTE Corporation")) {
//                    uiHandler.sendEmptyMessage(12);
//                }
//                isPlaying = false;
////                uiHandler.removeMessages(PLAY_TIME);
//            } else {
//                mVideoPlayerPause.setImageResource(R.mipmap.player_pause);
//                mVideoPlayerPause.setVisibility(View.VISIBLE);
//                mPlayListVideo.pause();
//                if (Build.MANUFACTURER.equals("ZTE Corporation")) {
//                    uiHandler.removeMessages(12);
//                }
//                isPlaying = true;
////                uiHandler.sendEmptyMessage(PLAY_TIME);
//            }
//        }
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
     * 结束全屏
     *
     * @return
     */
    private boolean stopFullVideo() {
        if (mFullVideo) {
            mFullVideo = false;
            //隐藏视频全屏时，窗口布局
            uiHandler.removeMessages(SHOW_CONTROL);
            uiHandler.sendEmptyMessage(HIDE_CONTROL);
//            mRlControl.setVisibility(View.GONE);
            //显示视频小窗口页面布局
            mLlPlayList.setVisibility(View.VISIBLE);
            mIvPlayListVideoUpInfo.setVisibility(View.VISIBLE);
            mIvPlayListVideoDownInfo.setVisibility(View.VISIBLE);
            mLlPlayListSeekBar.setVisibility(View.VISIBLE);
            //设置全屏参数
            mLlPlayVideo.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 2.0f));
            LinearLayout.LayoutParams mLlPlayListParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 5);
            mLlPlayListParams.setMargins(58, 34, 0, 40);
            mLlPlayList.setLayoutParams(mLlPlayListParams);

            LinearLayout.LayoutParams mLlPlayVideoParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
            mLlPlayVideoParams.setMargins(32, 72, 58, 30);
            mLlPlayVideo.setLayoutParams(mLlPlayVideoParams);

            return true;
        }
        return false;
    }

    /**
     * 全屏播放视频
     */
    private void fullVideo() {
//        showControl();
        mFullVideo = true;
        //隐藏视频小窗口布局
        mLlPlayListSeekBar.setVisibility(View.GONE);
        mLlPlayList.setVisibility(View.GONE);
        mIvPlayListVideoUpInfo.setVisibility(View.GONE);
        mIvPlayListVideoDownInfo.setVisibility(View.GONE);
        //显示视频全屏布局
        uiHandler.sendEmptyMessage(SHOW_CONTROL);
        uiHandler.sendEmptyMessageDelayed(HIDE_CONTROL,10000);

        mLlPlayVideo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mRlPlayVideo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
    }

    /**
     * 获取播放数据
     */
    private void initPlayDatas() {
        Intent intent = getIntent();
        mAlbumId = intent.getStringExtra("albumId");
//        String albumId = "1";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.PLAY_LIST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlayListService playListService = retrofit.create(PlayListService.class);
        Call<PlayListBean> trackListBeanCall = playListService.getPlayListData(
                mAlbumId,UserLauncherBean.getInstance().getUserName());
        trackListBeanCall.enqueue(new Callback<PlayListBean>() {
            @Override
            public void onResponse(Call<PlayListBean> call, Response<PlayListBean> response) {
                Log.e(TAG, "onResponse: 获取播放列表成功" + response.toString());
                PlayListBean playListBean = new PlayListBean();
                playListBean = response.body();
                mTrackList = playListBean.getData().getTrackList();
                for (int i = 0; i < mTrackList.size(); i++) {
                    mTrackName.add(mTrackList.get(i).getTrackName());
                    mVodId.add(mTrackList.get(i).getVodId());
                }
                if (mListView.getVisibility() == View.VISIBLE) {
                    mListView.setFocusable(true);
                    mPlayListVideo.setFocusable(false);
                    PlayListAdapter adapter = new PlayListAdapter(mTrackList, PlayListActivity.this);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<PlayListBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 获取播放列表数据失败");
            }
        });
    }

    public static void actionStart(Context context, String albumId) {
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtra("albumId", albumId);
        context.startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemSelected: " + position);
        //暂停时，点击上下播放，隐藏暂停图标
        if (mVideoPlayerPause.getVisibility()==View.VISIBLE){
            mVideoPlayerPause.setVisibility(View.GONE);
            isPlaying=false;
        }
        mListPosition = position;
        setListPage(position);//设置列表页数
        mTrackListBean = mTrackList.get(position);
        mTvTitle.setText(mTrackList.get(position).getTrackName());
        getPlayUrl();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 获取播放URL
     */
    private void getPlayUrl() {

        Log.e(TAG, "getPlayUrl:222 "+mListPosition);
        String mContentId=mVodId.get(mListPosition);
        Log.e(TAG, "getPlayUrl: "+mContentId);

        if (mPlatform == null) {
            mPlatform = "GD";
        }
        String mBeginTime = "";
        String mEndTime = "";
        long mTime = System.currentTimeMillis();                                     //获取时间戳
        String mRiddle = LnMD5Utils.MD5(mTime + AppCommonInfo.PLAY_KEY);           //加密串加密串（时间戳+key的md5值），
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
//                        Log.e(TAG, "run：请求视频url网络" + mLnPlayUrlBean.getPlayUrl().toString());
                        if (mLnPlayUrlBean == null)
                            return;
                        getPlatform(mLnPlayUrlBean);       //播放视频
//                        beginPlayVideo();//测试使用
                    }
                });
            }

            @Override
            public void onFailure(Call<LnPlayUrlBean> call, Throwable t) {
                Log.e(TAG, "onFailure: 请求视频链接失败！");
            }
        });
    }

    /**
     * 判断平台是否可以播放视频
     *
     * @param lnPlayUrlBean
     */
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
                beginPlayVideo(playUrl);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void beginPlayVideo(Uri playUrl) {
//        uiHandler.removeMessages(11);
//        mStayTime = 0;
//        uiHandler.sendEmptyMessage(11);
//
//        Log.e(TAG, "beginPlayVideo: " + mIsFree + ":" + mUserCode);
//
//        if (mHasObtainIsFree) {
//            mVideoView.setVideoPath(playUrl.toString());
//            mHasObtainIsFree = false;
//        } else {
//            if (mIsFree == 0 && mUserCode.equals("201")) {//免费视频，和未定够状态提示订购
//                AlertDialog.Builder builder = new AlertDialog.Builder(LnPlayVideoActivity.this);
//                builder.setMessage("您尚未订购该产品，请订购后，继续收看该节目！")
//                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                return;
//            } else if (mIsFree == 1 || mUserCode.equals("200")) {//如果是白名单，直接播放
//                //正式地址
//                mVideoView.setVideoPath(playUrl.toString());
//            }
//        }

        mPlayListVideo.setVideoPath(playUrl.toString());
//        mPlayListVideo.setVideoPath("http://vfx.mtime.cn/Video/2016/04/21/flv/160421132659681581.flv");
        mPlayListVideo.setOnPreparedListener(this);
        mPlayListVideo.setOnCompletionListener(this);
//        mVideoView.requestFocus();

    }


    /**
     * 设置列表页数
     *
     * @param position
     */
    private void setListPage(int position) {
        String listPage;
        if ((mTrackList.size() % 5) > 0) {
            listPage = (position / 5 + 1) + "/" + (mTrackList.size() / 5 + 1);
        } else {
            listPage = (position / 5 + 1) + "/" + (mTrackList.size() / 5);
        }
        mTvListPage.setText(listPage);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp.isPlaying())
            mp.reset();
        mp.start();
        //获取视频总时间
        mDuration = mPlayListVideo.getDuration();
        mTvPlayListRightTime.setText(LnUtils.generateTime(mDuration));//显示视频总时长
        mTvDurRight.setText(LnUtils.generateTime(mDuration));//显示视频总时长
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (!Build.MANUFACTURER.equals("ZTE Corporation")) {
                    //当前播放时间获取
                    mCurrentTime = mPlayListVideo.getCurrentPosition();
                    mSbPlayListSeekBar.setProgress((int) ((mCurrentTime * 1000) / mDuration)); //进度条
                    mVideoPlayerProgress.setProgress((int) ((mCurrentTime * 1000) / mDuration)); //进度条
                    mTvPlayListLeftTime.setText(LnUtils.generateTime(mCurrentTime));
                    mTvDurLeft.setText(LnUtils.generateTime(mCurrentTime));
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
//        mIvVideoBg.setVisibility(View.VISIBLE);
        mTvDurLeft.setText("00:00");
        mTvDurRight.setText("00:00");
        playNext();          //播放完成，自动播放下一个节目
    }

    private void playNext() {
        Log.e(TAG, "playNext: ");
//        addPlayRecord();     //添加播放记录
        if (Build.MANUFACTURER.equals("ZTE Corporation")) {
            mZTETime = 0;
        }
        mListPosition++;
        mListView.setSelection(mListPosition);
        playVodByIndex();
    }

    private void playVodByIndex() {
        Log.e(TAG, "playVodByIndex: "+mListPosition+":"+playVodIdList.size());
        if (mListPosition >= mTrackList.size()) {
            finish();
            return;
        }

        isPlaying = false;
//        mLlLoadingLayout.setVisibility(View.VISIBLE);
//        mIvVideoBg.setVisibility(View.VISIBLE);
        uiHandler.removeMessages(PLAY_TIME);
        mTvDurLeft.setText("00:00");
        if (mPlayListVideo != null) {
            mPlayListVideo.stopPlayback();
        }
        getPlayUrl();
//        showControl();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick: " + position);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "onKey: " + keyCode);
        if (KeyEvent.ACTION_DOWN == event.getAction() && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                setPauseOrPlay();
        }
        return false;
    }
}
