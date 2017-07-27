package com.guanghua.ln.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/6/2 0002.
 */

public class LnUtils {

    //int时间转换为视频播放进度 01:20:30这种   毫秒单位
    public static String generateTime(long time) {
        int totalSeconds = (int) (time/1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    //int时间转换为视频播放进度 01:20:30这种   毫秒单位
    public static String generateTimeInt(int time) {
//        int totalSeconds = (int) (time/1000);
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * MD5加密
     * @param string
     * @return
     */
    public static String MD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
