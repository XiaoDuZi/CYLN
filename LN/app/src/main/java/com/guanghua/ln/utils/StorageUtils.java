package com.guanghua.ln.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Godfather on 16/4/bg.
 * <p/>
 * SD卡存储目录
 */
public class StorageUtils {

    //--------------------app存储目录获取管理------start

    /**
     * 通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/{package_name}/files/ ，储存一些长时间保存的数据；
     * <p/>
     * 通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/{package_name}/cache/，储存临时缓存数据；
     * <p/>
     * 这两个目录分别对应 设置->应用->应用详情里面的”清除数据“与”清除缓存“选项。
     **/

    /**
     * 获取app所在的cache缓存目录 返回:/storage/emulated/0/Android/data/com.cq.ln/cache
     *
     * @param context
     * @return
     */
    public static String getAppCacheDir(Context context) {
        if (!isMounted())
            return "";
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/cache").append(File.separator).toString();
        }
        return sb.toString();
    }

    /**
     * @param context 获取crash目录 返回:/storage/emulated/0/Android/data/com.cq.ln/files/crash
     * @return
     */
    public static String getAppCrashDir(Context context) {
        if (!isMounted())
            return "";
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir("crash");
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/files/crash").append(File.separator).toString();
        }
        return sb.toString();
    }

    public static String getAppCameraDir(Context context) {
        if (!isMounted())
            return "";
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir("camera");
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/files/camera").append(File.separator).toString();
        }
        return sb.toString();
    }

    private static boolean isMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    //--------------------app存储目录获取管理------end

}
