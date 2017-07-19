package com.guanghua.ln.interfaces;


import com.guanghua.ln.bean.AuthenticationBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public interface ProgramIDAuthenticationService {
    @GET("authenticationOtt_spAuthority.do")
//    @GET("authenticationOtt_authorityForSp.do")
    Call<AuthenticationBean> getResult(
            @Query("temptoken") String temptoken, @Query("programid") String programid,
            @Query("spid") String spid, @Query("time") long time,
            @Query("riddle") String riddle
    );
}
