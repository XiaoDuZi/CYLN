package com.guanghua.ln.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guanghua.ln.R;
import com.guanghua.ln.bean.PlayListBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class PlayListTitleAdapter extends BaseAdapter {

    private static final String TAG = "PlayListTitleAdapter";

    private List<PlayListBean.DataBean.TrackListBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public PlayListTitleAdapter(List<PlayListBean.DataBean.TrackListBean> list, Context context) {
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
            convertView = mLayoutInflater.inflate(R.layout.play_list_video_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlayListBean.DataBean.TrackListBean trackListBean = mList.get(position);
        viewHolder.mTvPlayListTrackName.setText(trackListBean.getTrackName());

        return convertView;
    }



    static class ViewHolder {
        @BindView(R.id.tv_play_list_track_name)
        TextView mTvPlayListTrackName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
