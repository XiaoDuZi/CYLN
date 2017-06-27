package com.guanghua.ln.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/24 0024.
 * http://120.76.221.222/CQTVWeb_edu/tvutvgo/playHistory.action?keyNo=1111&recordId=3
 * &videoId=1&contentId=11&videoName=%E5%B0%8F%E6%98%9F%E6%98%9F&playTime=111&stayTime=1111
 */

public interface PlayRecordService {
    @GET("playHistory.action")
    Call<String> getPlayRecord(@Query("keyNo") String keyNo, @Query("recordId") int recordId,
                                   @Query("videoId") String videoId, @Query("content_Id") String contentId,
                                   @Query("videoName") String videoName, @Query("playTime") String playTime,
                                   @Query("stayTime") String stayTime);
}
