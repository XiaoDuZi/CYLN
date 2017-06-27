package com.guanghua.ln.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by YS on 2016/2/26.
 * 类说明：工具类
 */

public class Tools {


    private static long lastClickTime;// 记录最后一次点击时间
    /**
     * 内存卡地址
     */
    public static final String SdCardPathRoot = Environment.getExternalStorageDirectory().getPath() + "/_hifi/";
    public static final String SdCardCachePath = SdCardPathRoot + "cache/";
    public static final String SdCardCacheFolder = SdCardPathRoot + "cache";
    public static final String SdCardCameraPath = SdCardPathRoot + "pic/";
    public static final String SdCardChat = SdCardPathRoot + "chat/";
    public static final String SdCardCrashRoot = SdCardPathRoot + "Crash/";


    /**
     * 获取app所在的cache缓存目录
     *
     * @param context
     * @return
     */
    public static String getAppCacheDir(Context context) {
        if (!isMounted())
            return null;
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/cache/").append(File.separator).toString();
        }
        return sb.toString();
    }
//    通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/{package_name}/files/ ，储存一些长时间保存的数据；
//
//    通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/{package_name}/cache/，储存临时缓存数据；
//
//    这两个目录分别对应 设置->应用->应用详情里面的”清除数据“与”清除缓存“选项。

    /**
     * @param context 获取crash目录
     * @return
     */
    public static String getAppCrashDir(Context context) {
        if (!isMounted())
            return null;
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir("crash");
        if (file != null) {
            sb.append(file.getAbsolutePath()).append(File.separator);
        } else {
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                    .append("/crash/").append(File.separator).toString();
        }
        return sb.toString();
    }


    private static boolean isMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    private static Toast longToast;
    private static HiFiDialogTools hiFiDialogTools = new HiFiDialogTools();
    // 显示提示信息（Toast）
    public static void showTip(Context context, String tip) {
        if (TextUtils.isEmpty(tip))
            return;
        hiFiDialogTools.showtips(context, tip, null);
    }

    // 显示提示信息（Toast）
    public static void showTip(Context context, int tip) {
        if (tip == -1)
            return;
        showTip(context, context.getResources().getString(tip));
    }


    // 显示提示信息（Toast）
    public static void showLongTip(Context context, String tip) {
        if (TextUtils.isEmpty(tip))
            return;
        if (longToast == null) {
            longToast = Toast.makeText(context, tip, Toast.LENGTH_LONG);
        } else {
            longToast.setText(tip);
        }
        longToast.show();
    }


    // 广播通知--------------------------------------------------------start
    public static final void SendBroadcastMsg(Activity activity, String action) {
        Intent intent = new Intent(action);
        activity.sendBroadcast(intent);
    }

    public static final void SendBroadcastMsg(Context context, String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    public static final void SendBroadcastMsg(Activity activity, Intent intent, String action) {
        intent.setAction(action);
        activity.sendBroadcast(intent);
    }

    public static final void SendBroadcastMsg(Context context, Intent intent, String action) {
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    public static final void RegisterBroadcastReceiver(Activity activity, BroadcastReceiver receiver, String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        activity.registerReceiver(receiver, intentFilter);
    }

    public static final void RegisterBroadcastReceiver(Context context, BroadcastReceiver receiver, String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        context.registerReceiver(receiver, intentFilter);
    }

    // 广播通知--------------------------------------------------------end

    /**
     * 判断SD卡是否已插入(是否已准备)
     *
     * @return
     */
    public static boolean getExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 查看SD卡的剩余空间(单位为MB)
     *
     * @return
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 查看SD卡总容量
     *
     * @return
     */
    public static long getSDAllSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 字符串格式化
     */
    public static String stringFormat(String formatedStr, Object... objects) {
        if (isNullOrEmpty(formatedStr))
            return "";

        return java.text.MessageFormat.format(formatedStr, objects);
    }

    /**
     * 构造URL编码
     */
    public static String makeUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            return URLEncoder.encode(value);
        }
    }

    /**
     * 是否快速点击
     *
     * @return
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD > 0 && timeD < 400) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastClick(long mill) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD > 0 && timeD < mill) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 生成Json对象
     *
     * @param str
     * @return
     */
    public static JSONObject getJSONObject(String str) {

        if (str == null || str.trim().length() == 0)
            return null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace(System.err);
        }
        return jsonObject;
    }

    /**
     * 去零，保留两位小数，如果是.00，则去掉零
     *
     * @param d
     * @return
     */
    public static final String FormatToStr(double d) {
        DecimalFormat df = new DecimalFormat("##.##");
        String s = df.format(d);
        return s;
    }

    /**
     * 保留两位小数
     *
     * @param d
     * @return
     */
    public static final double FormatTwoDecimal(double d) {
        DecimalFormat df = new DecimalFormat("##.00");
        String s = df.format(d);
        double dou = Double.parseDouble(s);
        return dou;
    }

    /**
     * 保留两位小数
     *
     * @param d
     * @param0 阿拉伯数字，如果不存在则显示为0
     * @param# 阿拉伯数字，如果不存在则显示为空
     * 0.19---->0.19
     * 0.0--->0.00
     * 15.4--->15.40
     */
    public static final String FormattwoDecimal(double d) {

        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d);
    }

    public static final String FormattwoDecimal(String str) {
        try {
            return FormattwoDecimal(Double.parseDouble(str));
        } catch (Exception e) {
            return str;
        }
    }

    public static final String FormattwoDecimal(float f) {
        try {
            return FormattwoDecimal(((double) f));
        } catch (Exception e) {
            return String.valueOf(f);
        }
    }

    public static final double FormatTwoDecimal(float d) {
        return FormatTwoDecimal(Double.parseDouble(d + ""));
    }

    /**
     * 去零，保留两位小数，如果是.00，则去掉零
     *
     * @param d
     * @return
     */
    public static final String FormatToStr(float d) {
        DecimalFormat df = new DecimalFormat("##.##");
        String s = df.format((double) d);
        return s;
    }

    // 去零（保留一位小数）
    public static final String FormatDoubleToStrone(double d) {
        DecimalFormat df = new DecimalFormat("#0.0");
        String s = df.format(d);

        return s;
    }

    // （保留一位小数）
    public static final String FormatFloatToStrOne(float d) {
        DecimalFormat df = new DecimalFormat("#0.0");
        String s = df.format((double) d);

        return s;
    }

    /**
     * 使用正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String SubZeroAndDot(String s) {
        if (s.indexOf(".") == 0)
            s = "0" + s;
        if (s.indexOf(".") != -1)
            s = s.replaceAll("\\.?0+?$", "");// 去掉多余的.00
        if (s.equals(""))
            s = "0";
        return s;
    }

    /**
     * 读取资源文件（Assets）
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取资源文件（Raw）
     *
     * @param context
     * @param rawID
     * @return
     */
    public static String getFromRaw(Context context, int rawID) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().openRawResource(rawID));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 随机生成字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {

        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);// 0~61
            sf.append(str.charAt(number));

        }
        return sf.toString();
    }

    /**
     * 获取手机唯一编码
     *
     * @param resolver
     * @return
     */
    public static final String AndroidID(ContentResolver resolver) {
        String phoneCode = android.provider.Settings.System.getString(resolver, android.provider.Settings.System.ANDROID_ID);
        return phoneCode;
    }

    /**
     * 格式化字符串--在字符串前面添加符号
     *
     * @param str
     * @param replaceChar
     * @return
     */
    public static String formatStringAddSymbol(String str, String replaceChar) {
        return replaceChar + " " + str.replace(replaceChar, "");
    }

    /**
     * 格式化字符串--在字符串前面去掉符号
     *
     * @param str
     * @param replaceChar
     * @return
     */
    public static String formatStringNoSymbol(String str, String replaceChar) {
        return str.replace(replaceChar, "");
    }

    /**
     * 是否空字符串
     *
     * @param input
     * @return
     */
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.length() == 0 || input.equals("[]") || input.equals("null");
    }

    // 左补0
    public static String padLeft(int number, int length) {
        return String.format("%0" + length + "d", number);
    }

    // 获取本地当前时间
    public static Date getLocalTime() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public static long getCurrentTime() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static String formatTime(long l) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
        return formatter.format(l);
    }

    // 获取本地当前时间 格式为：yyyy-MM-dd HH:mm:ss
    public static String getLocalStrTime() {
        Calendar c = Calendar.getInstance();
        return ConverDataToStr(c.getTime(), null);
    }

    // 格式化时间显示（聊天记录使用）
    public static String formatTimeToNameView(Date date) {
        String tName = "", dName = "";
        String strDate = ConverDataToStr(date, "HH:mm");
        Calendar today = Calendar.getInstance();
        int cD = today.get(Calendar.DAY_OF_MONTH);
        int mD = DateToCalendar(date).get(Calendar.DAY_OF_MONTH);
        if (mD == cD) {
            dName = "今天";
        } else if (mD == cD - 1) {
            dName = "昨天";
        } else if (mD == cD - 2) {
            dName = "前天";
        } else {
            return ConverDataToStr(date, "MM-dd HH:mm");
        }
        int mH = date.getHours();
        if (mH > 6 && mH <= 10) {
            tName = "早上";
        } else if (mH > 10 && mH <= 12) {
            tName = "上午";
        } else if (mH > 12 && mH <= 18) {
            tName = "下午";
        } else if (mH > 18 && mH <= 6) {
            tName = "晚上";
        }

        if (!isNullOrEmpty(dName))
            dName += " ";

        return dName + tName + strDate;// dName + tName + strDate;
    }

    /**
     * 格式化时间显示（查看附近的人使用）
     **/
    public static String formatTimeToNearView(Date date) {
        Calendar today = Calendar.getInstance();
        int cD = today.get(Calendar.DAY_OF_MONTH);
        int mD = DateToCalendar(date).get(Calendar.DAY_OF_MONTH);
        if (mD == cD) {
            if (today.getTime().getHours() == date.getHours()) {
                return Math.abs((today.getTime().getMinutes() - date.getMinutes())) + "分钟前";
            } else
                return Math.abs((today.getTime().getHours() - date.getHours())) + "小时前";
        } else if (Math.abs(cD - mD) < 6) {
            return Math.abs(cD - mD) + "天前";
        } else {
            return "6天以前";
        }
    }

    // 格式化时间显示(查看好友圈使用)
    public static String formatTimeToFriendsBlogs(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar currentDate = DateToCalendar(date);

        long diff = today.getTime().getTime() - currentDate.getTime().getTime();// 不够24小时的，diff=0
        long diffDay = diff / (1000 * 60 * 60 * 24);
        if (currentDate.get(Calendar.HOUR_OF_DAY) * 60 + currentDate.get(Calendar.MINUTE) > today.get(Calendar.HOUR_OF_DAY) * 60 + today.get(Calendar.MINUTE)) {
            diffDay += 1;
        }

        if (diffDay == 0) {

            long diffHour = (diff / (60 * 60 * 1000)) - diffDay * 24;
            long diffMin = (diff / (60 * 1000)) - diffDay * 24 - diffHour * 60;
            if (diffHour > 0) {
                return diffHour + "小时前";
            } else {
                if (diffMin <= 1) {
                    return "刚刚";
                } else {
                    return diffMin + "分钟前";
                }
            }

        } else if (diffDay == 1) {// 超过24小时的
            return "昨天";
        } else {
            return diffDay + "天前";
        }
    }

    // 格式化时间日期，用于即时预订用
    public static String formatTimeToSaveOrder(String time) throws Exception {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = oldFormat.parse(time);
        return newFormat.format(date);
    }

    /**
     * @param timeStamp 格式化交易时间，支持参数格式为时间戳 201409201721
     * @return 返回格式 2014-09-20 17:21
     */
    public static String formatTimeToTransTime(String timeStamp) {
        StringBuffer result = new StringBuffer();
        result.append(timeStamp.substring(0, 4) + "-");
        result.append(timeStamp.substring(4, 6) + "-");
        result.append(timeStamp.substring(6, 8) + " ");
        result.append(timeStamp.substring(8, 10) + ":");
        result.append(timeStamp.substring(10, 12));

        return result.toString();

    }

    /**
     * 将timestamp转换成date
     *
     * @param tt
     * @return
     * @author hellostoy
     */
    public static Date timestampToDate(Timestamp tt) {
        return new Date(tt.getTime());
    }

    /**
     * 把日期转为字符串
     *
     * @param date
     * @param formatStr
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String ConverDataToStr(Date date, String formatStr) {
        if (isNullOrEmpty(formatStr))
            formatStr = "yyyy-MM-dd HH:mm:ss";

        DateFormat df = new SimpleDateFormat(formatStr);

        return df.format(date);
    }

    /**
     * 把字符串转为日期
     *
     * @param strDate
     * @param formatStr
     * @return 2014-05-09 14:46:18
     */
    @SuppressLint("SimpleDateFormat")
    public static Date ConverStrToDate(String strDate, String formatStr) {
        if (isNullOrEmpty(strDate))
            return new Date();
        if (strDate.indexOf("T") != -1)
            strDate = strDate.replace("T", " ");
        if (!isNullOrEmpty(strDate) && strDate.indexOf('/') != -1)
            strDate = strDate.replace('/', '-');
        if (isNullOrEmpty(formatStr)) {
            if (strDate.indexOf(" ") == -1)// 不存在空格" "
                formatStr = "yyyy-MM-dd";
            else
                formatStr = "yyyy-MM-dd HH:mm:ss";
        }
        Date d = null;
        try {
            DateFormat df = new SimpleDateFormat(formatStr);
            d = df.parse(strDate);
        } catch (ParseException ex) {
        }
        return d;
    }


    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time 返回密码：mm:ss
     * @return
     */
    public static String convertLong2Time(long time) {
        String format = "mm:ss";
        if (time > 0l) {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }
        return "";
    }


    /**
     * 获取当前时间,精确到秒，返回格式String字符串：yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTimeStr() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime);
    }

    public static String getCurrentTimeStr2() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    /**
     * 获取自己需要的时间格式，传入的时间格式支持如下： yyyy-MM-dd HH:mm:ss、yyyy/MM/dd
     * HH:mm:ss、yyyyMMddHHmmss
     *
     * @param pattern
     * @return
     */
    public static String getCurrentTimeStr(String pattern) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(currentTime);
    }

    /**
     * 获取当前时间,精确到毫秒，返回格式String字符串：yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String getCurrentTimeExactStr() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return formatter.format(currentTime);
    }

    /**
     * 获取当前日期，返回格式 yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    /**
     * 获取订单唯一编码 格式：yyyyMMddHHmmssXXXXXX
     *
     * @return
     */
    public static String getVerificationCode() {
        return getCurrentTimeStr() + getRandom();
    }

    /**
     * 获取六位随机数
     *
     * @return
     */
    private static String getRandom() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int n = (int) (Math.random() * 10);
            if (n == 10)
                n -= 1;
            sb.append(n);
        }
        return sb.toString();
    }

    /**
     * 将String型格式化,比如想要将2011-11-11格式化成2011年11月11日
     *
     * @param date       想要格式化的日期
     * @param oldPattern String 想要格式化的日期的现有格式
     * @param newPattern String 想要格式化成什么格式
     * @return String
     */
    public final static String StringDateFormat(String date, String oldPattern, String newPattern) {
        if (date == null || newPattern == null)
            return "";
        if (date.indexOf("T") != -1)
            date = date.replace("T", " ");
        if (isNullOrEmpty(oldPattern)) {
            if (date.indexOf(" ") == -1)
                oldPattern = "yyyy-MM-dd";
            else
                oldPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern, Locale.getDefault()); // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern, Locale.getDefault()); // 实例化模板对象
        Date d = null;
        try {
            d = sdf1.parse(date); // 将给定的字符串中的日期提取出来
        } catch (Exception e) { // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace(); // 打印异常信息
        }
        return sdf2.format(d);
    }

    /**
     * 日期转日历
     *
     * @param date
     * @return Calendar
     */
    public static Calendar DateToCalendar(Date date) {
        Calendar startdate = Calendar.getInstance();
        startdate.setTime(date);
        return startdate;
    }

    /**
     * 日历转日期
     *
     * @param calendar
     * @return Date
     */
    public static Date CalendarToDate(Calendar calendar) {
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 去掉多余的T00:00:00
     *
     * @param datestr
     * @return
     */
    public static String SubDatestr(String datestr) {
        if (Tools.isNullOrEmpty(datestr))
            return "";
        if (datestr.indexOf("T") != -1)
            return datestr.substring(0, datestr.indexOf("T"));
        else
            return datestr;
    }

    /**
     * 根据date获取星期
     *
     * @param date
     * @return
     */
    public static String GetWeek(Date date) {
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar objCalendar = Calendar.getInstance();
        objCalendar.setTime(date);
        int dayOfWeek = objCalendar.get(Calendar.DAY_OF_WEEK);
        return dayNames[dayOfWeek - 1];
    }

    /**
     * 为程序创建桌面快捷方式
     *
     * @param context
     */
//	public static void addShortcut(Context context) {
//		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//		// 快捷方式的名称
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
//		shortcut.putExtra("duplicate", false); // 不允许重复创建
//		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
//		shortcutIntent.setClassName(context, Welcome.class.getName());
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//		// 快捷方式的图标
//		Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, R.drawable.icon);
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//
//		context.sendBroadcast(shortcut);
//	}


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取手机屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getDisplayHeight(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取手机屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getDisplayWidth(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }


    /**
     * 获取手机屏幕分辨率dpi
     *
     * @param context
     * @return
     */
    public static int getDisplayDensity(Context context) {

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);

        int width = metric.widthPixels; // 宽度（PX）
        int height = metric.heightPixels; // 高度（PX）

        float density = metric.density; // 密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 密度DPI（120 / 160 / 240/320）

        // 对应关系
        // Android手机屏幕标准 对应图标尺寸标准 屏幕密度
        // xxhdpi 480
        // xhdpi 124*600 96*96 320
        // hdpi 480*800 72*72 240
        // mdpi 480*320 48*48 160
        // ldpi 320*240 36*36 120

        return densityDpi;

    }

    /**
     * 隐藏键盘
     *
     * @param activity
     */
    public static void HideSoftInput(Activity activity) {
        if (activity == null)
            return;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     */
    public static void HideSoftInput(Context context) {
        if (context == null)
            return;
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void HideSoftInput(Context context, EditText view) {
        if (context == null || view == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘，点击外部自动隐藏（推荐使用）
     *
     * @param editText
     */
    public static void ShowSoftInput(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     *
     * @param activity
     */
    public static void ShowSoftInput(Activity activity) {
        if (activity == null)
            return;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param view
     */
    public static void ShowSoftInput(Context context, EditText view) {
        if (context == null || view == null)
            return;
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 网络连接失败（线程内使用）
     *
     * @param context
     */
    public static void ErrNetWorkState(final Context context) {
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "网络不给力~ 请检查网络设置。", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }

    /**
     * 数据解析错误提示（线程内使用）
     *
     * @param context
     */
    public static void ErrParseData(final Context context) {
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "数据解析有误！请重试。", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }


    /**
     * 获取手机状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    // android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // 获取当前活动Activity Class Name
    public static String getTopActivityClassName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    // 判断程序（App）是否在前台运行
    public static boolean isTopActivity(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            // 应用程序位于堆栈的顶层
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取栈顶Activity名称
     *
     * @param context
     * @return
     */
    public static String getCurrentActivityName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);

        RunningTaskInfo cinfo = runningTasks.get(0);
        ComponentName component = cinfo.topActivity;


        return component.getClassName();
    }


    // 获取签名公钥（失败返回null）
    public static String getPublicKey(Context context) {
        byte[] signature = null;
        try {
            // 获取签名信息
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (info != null && info.signatures != null)
                signature = info.signatures[0].toByteArray();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (signature == null)
            return null;
        try {
            // 获取公钥
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));

            RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
            BigInteger big = publicKey.getModulus();

            return big.toString(16);// 返回十六进制
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    // SharedPreferences-------------------------------------------------------start
    public static String getStringPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(tag, "");
    }

    public static void setStringPreference(Context context, String tag, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    public static int getIntegerPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(tag, 0);
    }

    public static int getIntegerPreference(Context context, String tag, int defaultInt) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(tag, defaultInt);
    }


    public static void setIntegerPreference(Context context, String tag, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putInt(tag, value);
        editor.commit();
    }

    public static Long getLongPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(tag, 0);
    }

    public static void setLongPreference(Context context, String tag, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putLong(tag, value);
        editor.commit();
    }

    public static boolean getBooleanPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(tag, false);
    }

    public static void setBooleanPreference(Context context, String tag, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putBoolean(tag, value);
        editor.commit();
    }

    public static void setFloatPreference(Context context, String tag, float value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putFloat(tag, value);
        editor.commit();
    }

    public static float getFloatPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(tag, 0.0f);
    }

    // SharedPreferences-------------------------------------------------------end

    // Vibrate-------------------------------------------------------------------start
    public static void Vibrate(Context context) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(300);
    }

    public static void VibrateDouble(Context context) {
        long[] pattern = {100, 200, 500, 300};
        Vibrate(context, pattern, false);
    }

    public static void Vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(Context context, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    // Vibrate--------------------------------------------------------------------end

    // 获取屏幕是否打开（屏幕亮返回true，屏幕关闭返回false）
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    // 对StringBuffer 替换字符串
    public static StringBuffer SBReplace(StringBuffer sbf, String oldstr, String newstr) {
        String tempstr = sbf.toString();
        return new StringBuffer(tempstr.replace(oldstr, newstr));
    }


    // 获取当前IMSI
    public static String getIMSI(Context context) {
        TelephonyManager telManeger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// 获取手机电话服务
        return telManeger.getSubscriberId();// 获取手机SIM卡的imsi号（15位）
    }


    /**
     * 格式化手机号+电话号码 & 用于点击对话框弹出号码选择
     *
     * @param Mobile
     * @param ContactPhone
     * @return
     */
    public static String[] formatContactNum(String Mobile, String ContactPhone) {
        if (Mobile == null) {
            Mobile = "";
        }
        if (ContactPhone == null) {
            ContactPhone = "";
        }
        if (ContactPhone.contains(Mobile)) {
            Mobile = "";
        }
        String temp = Mobile + " " + ContactPhone;
        temp = temp.replaceAll("[，；;\\-\\ ]", ",");
        String[] phones = temp.split(",");
        ArrayList<String> phoness = new ArrayList<String>();
        for (int i = 0; i < phones.length; i++) {
            if (phones[i].length() < 3)
                continue;
            if (phones[i].length() < 5) {
                phones[i] = phones[i] + "" + phones[i + 1];
                phoness.add(phones[i]);
                i++;
            } else
                phoness.add(phones[i]);
        }
        String[] nums = new String[phoness.size()];
        nums = phoness.toArray(nums);
        return nums;

    }


    // 几种不同单位转换的方法（未使用）
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static void removeAllFile(Context context) {
        File folder = new File(StorageUtils.getAppCameraDir(context));
        File[] files = folder.listFiles();
        if (files.length > 0)
            for (File f : files) {
                if (f.getName().startsWith("IMG")) {
                    f.delete();
                }
            }
    }


    private static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 每输入四位数字加空格
     *
     * @param mEditText
     */
    public static void numAddSpace(final EditText mEditText) {
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = mEditText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    mEditText.setText(str);
                    Editable etable = mEditText.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
    }

    /**
     * 下划线
     *
     * @param v
     */
    public static void DrawUnderline(Button v) {
        v.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void DrawUnderline(TextView v) {
        v.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * 检验银行卡的正确性
     *
     * @param str
     * @return
     */
    public static boolean InspectBankCard(String str) {
        char[] cardNumArray = str.toCharArray();
        int[] n = new int[cardNumArray.length + 1];
        int j = 1;
        for (int i = cardNumArray.length - 1; i >= 0; i--) {
            n[j++] = cardNumArray[i] - '0';
        }
        if (Algorithm(n))
            return true;
        else
            return false;

    }

    /**
     * 检验合理性
     *
     * @param n
     * @return
     */
    private static boolean Algorithm(int[] n) {
        int even = 0;// 偶数位
        int odd = 0;// 奇数位
        for (int i = 1; i < n.length; i++) {
            if (i % 2 == 0) {// 偶数位
                int temp = n[i] * 2;
                if (temp < 10) {
                    even += temp;
                } else {
                    temp = temp - 9;
                    even += temp;
                }
            } else {// 奇数位
                odd += n[i];
            }
        }

        int total = even + odd;
        if (total % 10 == 0)
            return true;
        else
            return false;

    }


    /**
     * 是否能转换为double
     *
     * @param money
     * @return
     */
    public static boolean isDouble(String money) {
        boolean result = false;
        if (isNullOrEmpty(money))
            result = false;
        try {
            Double d = Double.parseDouble(money);
            result = true;
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    /**
     * 设置键盘输入模式
     */
    public static void SoftInputModel(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 遍历 设置布局里面的所有字控件Enable的属性
     */
    public static void setLayoutAllViewEnable(RelativeLayout layoutView, boolean enable) {
        for (int i = 0; i < layoutView.getChildCount(); i++) {
            View v = layoutView.getChildAt(i);
            setEnable(enable, v);

        }
    }

    /**
     * 遍历 设置布局里面的所有字控件Enable的属性
     */
    public static void setLayoutAllViewEnable(FrameLayout layoutView, boolean enable) {
        for (int i = 0; i < layoutView.getChildCount(); i++) {
            View v = layoutView.getChildAt(i);
            setEnable(enable, v);

        }
    }

    /**
     * 遍历 设置布局里面的所有字控件Enable的属性
     */
    public static void setLayoutAllViewEnable(RadioGroup layoutView, boolean enable) {
        for (int i = 0; i < layoutView.getChildCount(); i++) {
            View v = layoutView.getChildAt(i);
            setEnable(enable, v);

        }
    }

    /**
     * 遍历 设置布局里面的所有字控件Enable的属性
     */
    public static void setLayoutAllViewEnable(LinearLayout layoutView, boolean enable) {
        for (int i = 0; i < layoutView.getChildCount(); i++) {
            View v = layoutView.getChildAt(i);
            setEnable(enable, v);

        }
    }

    private static void setEnable(boolean enable, View v) {
        if (v instanceof LinearLayout)
            setLayoutAllViewEnable((LinearLayout) v, enable);
        if (v instanceof RelativeLayout)
            setLayoutAllViewEnable((RelativeLayout) v, enable);
        if (v instanceof FrameLayout)
            setLayoutAllViewEnable((FrameLayout) v, enable);
        if (v instanceof RadioGroup)
            setLayoutAllViewEnable((RadioGroup) v, enable);
        if (v instanceof Button)
            v.setEnabled(enable);
        if (v instanceof EditText)
            v.setEnabled(enable);
        if (v instanceof ImageView)
            v.setEnabled(enable);
        if (v instanceof TextView)
            v.setEnabled(enable);

        if (v instanceof View)
            v.setEnabled(enable);
        if (v instanceof CheckBox)
            v.setEnabled(enable);
        if (v instanceof RadioButton)
            v.setEnabled(enable);

        if (v instanceof ImageButton)
            v.setEnabled(enable);
    }

    /**
     * 是否能转为int型数据
     *
     * @param intstr
     * @return
     */
    public static boolean isCanParseInt(String intstr) {
        try {
            Integer.parseInt(intstr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static byte[] merge(byte[]... b) {
        int count = 0;
        for (byte[] mb : b) {
            count += mb.length;
        }
        byte[] result = new byte[count];
        int leng = 0;
        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, result, leng, b[i].length);
            leng += b[i].length;
        }
        return result;
    }

    public static byte[] str2byte(String str) {
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2str(byte[] data) {
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float getPrice(String vipprice, String price) {
        if (Tools.isNullOrEmpty(vipprice) && Tools.isNullOrEmpty(price))
            return 0;
        float t_price = Float.valueOf(Tools.isNullOrEmpty(price) ? "0" : price);
        float t_vipprice = Float.valueOf(Tools.isNullOrEmpty(price) ? "0" : vipprice);
        return t_vipprice > 0 ? t_vipprice : t_price;
    }
}
