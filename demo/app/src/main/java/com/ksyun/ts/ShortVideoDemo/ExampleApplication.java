package com.ksyun.ts.ShortVideoDemo;

import android.app.Application;

import com.ksyun.ts.ShortVideoDemo.ui.CrashHandler;
import com.ksyun.ts.shortvideo.common.util.KLog;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoAuth;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.util.ToastUtils;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by xiaoqiang on 2017/12/5.
 */

public class ExampleApplication extends Application {
    private static final String TAG = ExampleApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
//        setupLeakCanary();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

           /*
         * 初始化Bugly，需要传入注册时申请的APPID，第三个参数为SDK调试模式开关；
         * 建议在测试阶段建议设置成true，发布时设置为false。
         * Bugly为应用崩溃日志收集工具，开发者可根据实际情况选择不集成或依赖其它Bug收集工具
         */
        CrashReport.initCrashReport(getApplicationContext(), "4e98881bde", false);

        KSVSShortVideoKitManager.addAuthorizeListener(ExampleApplication.this, mAuthListener);

    }

    protected void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private IKSVSShortVideoAuth.IKSVSShortVideoAuthListener mAuthListener =
            new IKSVSShortVideoAuth.IKSVSShortVideoAuthListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailed(int error, String message) {
                    KLog.e(TAG, "鉴权失败,错误码：" + error + "，错误原因：" + message);
                    ToastUtils.showToast(ExampleApplication.this, R.string.login_auth_error);
                }
            };


}
