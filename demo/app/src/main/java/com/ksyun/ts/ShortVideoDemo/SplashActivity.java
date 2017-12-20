package com.ksyun.ts.ShortVideoDemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.ui.UserLocalization;
import com.ksyun.ts.shortvideo.common.util.KLog;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoAuth;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.util.ToastUtils;

/**
 * Created by xiaoqiang on 2017/11/30.
 */

public class SplashActivity extends BaseActivity {
    public final static String SDK_AUTH_TOKEN = "ff9737a56f0805482b1dff8fb662c784";
    private UserLocalization mUserLocal;
    private User mUser;
    private long mStartTime;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserLocal = new UserLocalization(this);
        mUser = mUserLocal.getData();
        mHandler = new Handler();
        if (mUser == null) {
            openLoginActivity();
        } else {
            sdkAuth();
        }
    }

    private void openMainActivity() {
        long time = 2 * 1000 - (System.currentTimeMillis() - mStartTime);
        if (time > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.openMainActivity(SplashActivity.this, mUser);
                    finish();
                }
            }, time);
        } else {
            MainActivity.openMainActivity(SplashActivity.this, mUser);
            finish();
        }

    }

    private void openLoginActivity() {
        long time = 2 * 1000 - (System.currentTimeMillis() - mStartTime);
        if (time > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, time);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void sdkAuth() {
        KSVSShortVideoKitManager.addAuthorizeListener(SplashActivity.this, mAuthListener);
        KSVSShortVideoKitManager.authorize(SplashActivity.this,
                SDK_AUTH_TOKEN, mUser.getToken());

    }

    private IKSVSShortVideoAuth.IKSVSShortVideoAuthListener mAuthListener =
            new IKSVSShortVideoAuth.IKSVSShortVideoAuthListener() {
                @Override
                public void onSuccess() {
                    openMainActivity();
                }

                @Override
                public void onFailed(int error, String message) {
                    KLog.e(TAG, "鉴权失败,错误码：" + error + "，错误原因：" + message);
                    ToastUtils.showToast(SplashActivity.this, R.string.login_auth_error);
                    // 不管是登录过程中鉴权失败。还是正在使用中出现鉴权失败。
                    openLoginActivity();
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KSVSShortVideoKitManager.removeAuthorizeListener(SplashActivity.this, mAuthListener);
    }
}
