package com.guanghua.ln.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.guanghua.ln.bean.RecordIDBean;
import com.guanghua.ln.bean.UserLauncherBean;
import com.guanghua.ln.common.AppCommonInfo;
import com.guanghua.ln.interfaces.LnRecordIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/6/23 0023.
 */

public class GetRecordId {

    private static final String TAG = "GetRecordId";

    private Context mContext;
    private Retrofit mRetrofit;
    private String mId;


    public String getRecord(){

        mRetrofit = new Retrofit.Builder()
                .baseUrl(AppCommonInfo.RECORDID_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LnRecordIdService mLnRecordIdService= mRetrofit.create(LnRecordIdService.class);
        Call<RecordIDBean> mRecordIDBeanCall=mLnRecordIdService.getRecordID("2","9950000002242817");
//                UserLauncherBean.getInstance().getUserName());
        mRecordIDBeanCall.enqueue(new Callback<RecordIDBean>() {

            @Override
            public void onResponse(Call<RecordIDBean> call, Response<RecordIDBean> response) {
                Log.e(TAG, "onResponse: 访问网络成功！");
                RecordIDBean recordIDBean=new RecordIDBean();
                recordIDBean=response.body();
                mId=recordIDBean.toString();
                Log.e(TAG, "onResponse: "+recordIDBean.toString());
            }

            @Override
            public void onFailure(Call<RecordIDBean> call, Throwable t) {
                Log.e(TAG, "onFailure: "+"请求网络失败");
            }
        });
        return mId;
    }

}
