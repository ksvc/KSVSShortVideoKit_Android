package com.ksyun.ts.ShortVideoDemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ksyun.ts.ShortVideoDemo.model.ErrorModel;
import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.request.RequestManager;
import com.ksyun.ts.ShortVideoDemo.request.RequestModel;
import com.ksyun.ts.ShortVideoDemo.ui.DefaultOnClick;
import com.ksyun.ts.ShortVideoDemo.ui.UserLocalization;
import com.ksyun.ts.shortvideo.common.util.KLog;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoAuth;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.common.KSVSDialodManager;
import com.ksyun.ts.skin.util.ToastUtils;

import io.reactivex.functions.Consumer;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class LoginActivity extends BaseActivity {

    private final static int MAX_TIMER = 60;
    private final static String USER_AUTH_TOKEN = "AKLTAkF1cPsvQsyn3DFZSxLBaf";
    private final static String USER_AUTH_SECRET = "ON8XoXxgwQ/WHgxKvKzkdryPs54AcK2NN/B/zzFMLgTawTv823lbjcnvSv/5Z3OC3w==";

    private EditText mPhoneEditor;
    private Button mBtnCaptcha;
    private Button mBtnLogin;
    private EditText mCaptcha;
    private Handler mHandler;
    private int mCurrentTimer = MAX_TIMER;
    private String mCaptchaHint;
    private RequestManager mRequestManager;
    private Dialog mLoadingDialog;
    private KSVSShortVideoKitManager mKitManager;
    private User mUser;

    public static void logout(Activity context) {
        new UserLocalization(context).clearAuthData();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mHandler = new Handler();
        mPhoneEditor = findViewById(R.id.edit_phone);
        mPhoneEditor.addTextChangedListener(mTextWatch);
        mBtnCaptcha = findViewById(R.id.btn_get_phone);
        mBtnLogin = findViewById(R.id.btn_login);
        mCaptcha = findViewById(R.id.edit_captcha);
        mBtnCaptcha.setOnClickListener(mOnClick);
        mBtnLogin.setOnClickListener(mOnClick);
        mCaptchaHint = getResources().getString(R.string.login_get_phone);
        mRequestManager = new RequestManager(LoginActivity.this);
    }

    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            mCurrentTimer--;
            if (mCurrentTimer <= 0) {
                mCurrentTimer = 60;
                if (!TextUtils.isEmpty(mPhoneEditor.getText().toString()) &&
                        mPhoneEditor.getText().toString().length() == 11) {
                    mBtnCaptcha.setEnabled(true);
                    mBtnCaptcha.setTextColor(getResources().getColor(R.color.login_get_phone_correct));
                }
                mBtnCaptcha.setText(mCaptchaHint);
            } else {
                mHandler.postDelayed(mTimer, 1000);
                mBtnCaptcha.setText(mCurrentTimer + "s");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KSVSShortVideoKitManager.removeAuthorizeListener(LoginActivity.this, mAuthListener);
        mCurrentTimer = MAX_TIMER;
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        if (mRequestManager != null) {
            mRequestManager.releaseAllReuqest();
            mRequestManager = null;
        }
    }

    private void sdkAuth() {
        KSVSShortVideoKitManager.addAuthorizeListener(LoginActivity.this, mAuthListener);
        KSVSShortVideoKitManager.authorize(LoginActivity.this,
                SplashActivity.SDK_AUTH_TOKEN, (mUser != null) ? mUser.getToken() : "");

    }

    private IKSVSShortVideoAuth.IKSVSShortVideoAuthListener mAuthListener =
            new IKSVSShortVideoAuth.IKSVSShortVideoAuthListener() {
                @Override
                public void onSuccess() {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                    if (mUser != null) {
                        new UserLocalization(LoginActivity.this).saveData(mUser);
                        MainActivity.openMainActivity(LoginActivity.this, mUser);
                        finish();
                    }
                }

                @Override
                public void onFailed(int error, String message) {
                    KLog.e(TAG, "鉴权失败,错误码：" + error + "，错误原因：" + message);
                    ToastUtils.showToast(LoginActivity.this, R.string.login_auth_error);
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                    // 不管是登录过程中鉴权失败。还是正在使用中出现鉴权失败。
                }
            };

    private DefaultOnClick mOnClick = new DefaultOnClick(null, new Consumer<View>() {
        @Override
        public void accept(View view) throws Exception {
            mUserToken = USER_AUTH_TOKEN;
            mUserSecret = USER_AUTH_SECRET;
            if (view.getId() == R.id.btn_get_phone) {   // 获取验证码
                mBtnCaptcha.setEnabled(false);
                mCaptchaHint = getResources().getString(R.string.login_reget_phone);
                mBtnCaptcha.setTextColor(getResources().
                        getColor(R.color.login_get_phone));
                mHandler.post(mTimer);
                mRequestManager.getPhoneVerification(mPhoneEditor.getText().toString(),
                        new RequestManager.IKSVSRequestListener<RequestModel>() {
                            @Override
                            public void onSuccess(RequestModel requestModel) {

                            }

                            @Override
                            public void onFailed(ErrorModel errorInfo) {
                                ToastUtils.showToast(LoginActivity.this,
                                        R.string.login_get_captcha_error);
                            }
                        });
            } else if (view.getId() == R.id.btn_login) {  // 登录
                final String phone = mPhoneEditor.getText().toString();
                String code = mCaptcha.getText().toString();
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    ToastUtils.showToast(LoginActivity.this, R.string.login_editor_phone_error);
                } else if (TextUtils.isEmpty(code) || code.length() != 4) {
                    ToastUtils.showToast(LoginActivity.this, R.string.login_editor_captcha_error);
                } else {
                    mLoadingDialog = KSVSDialodManager.showLoadingDialog(LoginActivity.this, null);
                    mRequestManager.login(phone, code, new RequestManager.IKSVSRequestListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            mUser = user;
                            sdkAuth();
                        }

                        @Override
                        public void onFailed(ErrorModel errorInfo) {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                                mLoadingDialog = null;
                            }
                            if (errorInfo.getCode().equalsIgnoreCase("VerificationCodeNotMatch")) {
                                ToastUtils.showToast(LoginActivity.this,
                                        R.string.login_captcha_error);
                            } else {
                                ToastUtils.showToast(LoginActivity.this,
                                        R.string.login_login_error);
                            }
                        }
                    });
                }
            }
        }
    });
    private TextWatcher mTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCaptchaHint = getResources().getString(R.string.login_get_phone);
            if (s.length() == 11 && mCurrentTimer == MAX_TIMER) {
                mBtnCaptcha.setEnabled(true);
                mBtnCaptcha.setTextColor(getResources().getColor(R.color.login_get_phone_correct));
            } else {
                mBtnCaptcha.setEnabled(false);
                mBtnCaptcha.setTextColor(getResources().getColor(R.color.login_get_phone));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
