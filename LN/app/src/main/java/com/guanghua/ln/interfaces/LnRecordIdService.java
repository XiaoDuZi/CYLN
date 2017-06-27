package com.guanghua.ln.interfaces;

import com.guanghua.ln.bean.RecordIDBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/23 0023.
 * http://192.168.34.145/CQTVWeb_edu/hifi/hifiData/single.action?singleId=42535&keyNo=9950000002242817
 */

public interface LnRecordIdService {
    @GET("single.action")
    Call<RecordIDBean> getRecordID(@Query("singleId") String singleId,
                                   @Query("keyNo") String keyNo);
}
