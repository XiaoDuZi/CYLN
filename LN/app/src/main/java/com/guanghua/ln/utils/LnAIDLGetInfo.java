package com.guanghua.ln.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bytuetech.aidl.UserInfo;
import com.example.aestest.AesIpoe;
import com.guanghua.ln.bean.UserLauncherBean;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * AIDL获取用户信息
 * Created by Administrator on 2017/5/31 0031.
 */

public class LnAIDLGetInfo {

    private static final String TAG = "LnAIDLGetInfo";

    private UserInfo userInfo;

    Context mContext;

    AppCompatActivity mActivity;

    public LnAIDLGetInfo(Context context) {
        mContext = context;
        mActivity= (AppCompatActivity) mContext;
        bindService();
    }

    private ServiceConnection conn = new ServiceConnection() {
        //绑定服务
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取到了远程的服务
            userInfo = UserInfo.Stub.asInterface(service);
            try {
                Log.e(TAG, "onServiceConnected: " + "获取用户信息成功");
                String userName = AesIpoe.desEncryptURLDecoder(userInfo.getUserName());
                String platform = userInfo.getPlatform();
                String user32Key = AesIpoe.desEncryptURLDecoder(userInfo.getUser32Key());
                String GroupId=userInfo.getGroupId();
                String PassWord=userInfo.getPassWord();
                String Multicast=userInfo.getMulticast();
                String TimeStamp=userInfo.getTimeStamp();
                String AreaCode=userInfo.getAreaCode();

                UserLauncherBean.getInstance().setUserName(userName);
                UserLauncherBean.getInstance().setPlatform(platform);
                UserLauncherBean.getInstance().setUser32Key(user32Key);
                UserLauncherBean.getInstance().setGroupId(GroupId);
                UserLauncherBean.getInstance().setPassWord(PassWord);
                UserLauncherBean.getInstance().setMulticast(Multicast);
                UserLauncherBean.getInstance().setTimeStamp(TimeStamp);
                UserLauncherBean.getInstance().setAreaCode(AreaCode);

                Log.e(TAG, "onServiceConnected: "+UserLauncherBean.getInstance().toString());

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //断开服务
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //回收服务
            userInfo = null;
        }
    };

    public void bindService() {
        Intent intent = new Intent("com.widgetdo.ottboxforgx.service.UserInfoService");
//        intent.setComponent(new ComponentName("com.widgetdo.ottboxforgx.service", "com.widgetdo.ottboxforgx.service.UserInfoService"));
        mActivity.bindService(intent, this.conn, BIND_AUTO_CREATE);
    }



}
