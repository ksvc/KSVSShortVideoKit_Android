package com.ksyun.ts.ShortVideoDemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class BaseActivity extends FragmentActivity {

    protected final static String TAG = BaseActivity.class.getName();
    protected final static String USER_PARAMS = "USER_PARAMS";
    protected static String mUserToken;
    protected static String mUserSecret;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 在5.0以上手机,因为设置的模式是status 栏透明，所以需要对Title视图增加Padding设置
     *
     * @return
     */
    protected int getBorderTop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDimensionPixelOffset(R.dimen.status_hight);
        } else {
            return 0;
        }
    }

    public static String getUserToken() {
        return mUserToken;
    }

    public static String getUserSecret() {
        return mUserSecret;
    }
}
