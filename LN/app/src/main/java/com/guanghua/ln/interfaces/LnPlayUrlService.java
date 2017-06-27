package com.guanghua.ln.interfaces;


import com.guanghua.ln.bean.LnPlayUrlBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 请求视频播放
 * Created by Administrator on 2017/5/27 0027.
 */

public interface LnPlayUrlService {
    @GET("returnPlayUrl.do")
    Call<LnPlayUrlBean> getPlayUrlInfo(@Query("type") int type, @Query("time") long time,
                                       @Query("riddle") String riddle, @Query("platform") String platform,
                                       @Query("spid") String spid, @Query("contentid") String contentid,
                                       @Query("begintime") String begintime, @Query("endtime") String endtime);

}
