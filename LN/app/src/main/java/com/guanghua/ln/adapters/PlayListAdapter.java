package com.guanghua.ln.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.guanghua.ln.R;
import com.guanghua.ln.bean.PlayListBean;
import com.guanghua.ln.common.AppCommonInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class PlayListAdapter extends BaseAdapter {


    private List<PlayListBean.DataBean.TrackListBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public PlayListAdapter(List<PlayListBean.DataBean.TrackListBean> list, Context context) {
        mList = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.play_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlayListBean.DataBean.TrackListBean trackListBean = mList.get(position);
        Uri uri = Uri.parse(AppCommonInfo.PLAY_LISI_IMAGE_URL + trackListBean.getTrackSmallImg());
        viewHolder.mIvPlayListVideoImage.setImageURI(uri);
        viewHolder.mTvPlayListVideoName.setText(trackListBean.getTrackName());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_play_list_video_image)
        SimpleDraweeView mIvPlayListVideoImage;
        @BindView(R.id.tv_play_list_video_name)
        TextView mTvPlayListVideoName;
        @BindView(R.id.rb_play_list_video_pause)
        RadioButton mBtPlayListVideoPause;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


//    class ViewHolder {
//        @BindView(R.id.iv_play_list_video_image)
//        SimpleDraweeView mIvPlayListVideoImage;
//        @BindView(R.id.iv_play_list_video_pause)
//        ImageView mIvPlayListVideoPause;
//        @BindView(R.id.tv_play_list_video_name)
//        TextView mTvPlayListVideoName;
//
//        ViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }
}


//        RecyclerView.Adapter<PlayListAdapter.ViewHolder> {


//    private List<PlayListBean.DataBean.TrackListBean> mPlayList;
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.iv_play_list_video_image)
//        SimpleDraweeView mIvPlayListVideoImage;
//        @BindView(R.id.tv_play_list_video_name)
//        TextView mTvPlayListVideoName;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    public PlayListAdapter(List<PlayListBean.DataBean.TrackListBean> playList) {
//        mPlayList = playList;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.play_list_item, parent, false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        PlayListBean.DataBean.TrackListBean trackListBean = mPlayList.get(position);
//        Uri uri = Uri.parse(AppCommonInfo.PLAY_LISI_IMAGE_URL+trackListBean.getTrackSmallImg());
//        Log.e(TAG, "onBindViewHolder: "+uri);
//        holder.mIvPlayListVideoImage.setImageURI(uri);
//        holder.mTvPlayListVideoName.setText(trackListBean.getTrackName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mPlayList.size();
//    }
//
//
//}
