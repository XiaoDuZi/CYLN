package com.guanghua.ln.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.guanghua.ln.R;
import com.guanghua.ln.adapters.PlayListAdapter;
import com.guanghua.ln.bean.PlayListBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.PlayListService;
import com.guanghua.ln.views.DividerItemDecoration;
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

public class PlayListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

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
    @BindView(R.id.rl_play_list)
    ListView mRlPlayList;

    private List<PlayListBean.DataBean.TrackListBean> mTrackList = new ArrayList<>();
    private boolean mGonePauseImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_play_list);
        ButterKnife.bind(this);
        initPlayDatas();
        mRlPlayList.setOnItemSelectedListener(this);
        mRlPlayList.setOnItemClickListener(this);
    }

    private void initPlayDatas() {

        Intent intent = getIntent();
//        String albumId = intent.getStringExtra("albumId");
        String albumId = "1";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.PLAY_LIST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlayListService playListService = retrofit.create(PlayListService.class);
        Call<PlayListBean> trackListBeanCall = playListService.getPlayListData(
                albumId, "9950000002581730");
//                UserLauncherBean.getInstance().getUserName());
        trackListBeanCall.enqueue(new Callback<PlayListBean>() {
            @Override
            public void onResponse(Call<PlayListBean> call, Response<PlayListBean> response) {
                Log.e(TAG, "onResponse: 获取播放列表成功" + response.toString());
                PlayListBean playListBean = new PlayListBean();
                playListBean = response.body();
                mTrackList = playListBean.getData().getTrackList();
                PlayListAdapter adapter = new PlayListAdapter(mTrackList,PlayListActivity.this);
                mRlPlayList.setAdapter(adapter);
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
        setListPage(position);
    }

    /**
     * 设置列表页数
     * @param position
     */
    private void setListPage(int position) {
        String listPage;
        if ((mTrackList.size()%5)>0){
            listPage = (position/5+1)+"/"+(mTrackList.size()/5+1);
        }else {
            listPage = (position/5+1)+"/"+(mTrackList.size()/5);
        }
        mTvListPage.setText(listPage);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRlPlayList.getItemsCanFocus();
        Log.e(TAG, "onItemClick: "+position);
//        ImageView pauseImage= (ImageView) findViewById(R.id.iv_play_list_video_pause);
//        if (mGonePauseImage){
//            pauseImage.setVisibility(View.GONE);
//            mGonePauseImage = false;
//        }else {
//            pauseImage.setVisibility(View.VISIBLE);
//            mGonePauseImage = true;
//        }

    }
}
