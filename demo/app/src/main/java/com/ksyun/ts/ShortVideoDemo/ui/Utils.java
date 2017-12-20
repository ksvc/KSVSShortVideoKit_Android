package com.ksyun.ts.ShortVideoDemo.ui;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class Utils {

    public static String getIMEI(Context context) {
//        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(
//                Context.TELEPHONY_SERVICE);
//        return TelephonyMgr.getDeviceId();
        String androidID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        return id;
    }

    /**
     * 视频临时目录，在应用程序退出时需要删除
     *
     * @param context
     * @return
     */
    public static String getTempLocalVideoPath(Context context) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory() + "/shortdemo");
        } else {
            file = new File(context.getFilesDir() + "/shortdemo");
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String computeMD5(String string) {
        try {
//            ON8XoXxgwQ/WHgxKvKzkdryPs54AcK2NN/B/zzFMLgTawTv823lbjcnvSv/5Z3OC3w==
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(string.getBytes());
            return bytesToHexString(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
