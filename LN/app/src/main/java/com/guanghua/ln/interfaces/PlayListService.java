package com.guanghua.ln.interfaces;

import com.guanghua.ln.bean.PlayListBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 视频播放列表
 * http://59.46.18.18/LNTVWeb_edu/hifi/hifiData/album.action?albumId=1&keyNo=9950000002581730
 * Created by Administrator on 2017/8/1 0001.
 */

public interface PlayListService {
    @GET("album.action")
    Call<PlayListBean> getPlayListData(@Query("albumId") String albumId,
                                                              @Query("keyNo") String keyNo);
}
