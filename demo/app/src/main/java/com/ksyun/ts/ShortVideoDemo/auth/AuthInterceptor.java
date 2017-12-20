package com.ksyun.ts.ShortVideoDemo.auth;

import android.content.Context;
import android.os.Build;

import com.ksyun.ts.ShortVideoDemo.BaseActivity;
import com.ksyun.ts.ShortVideoDemo.ui.Utils;
import com.ksyun.ts.shortvideo.common.util.SystemUtil;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xiaoqiang on 2017/11/22.
 */

public class AuthInterceptor implements Interceptor {

    private Context mContext;
    private static final String SIGNSTRING_LINKER = "\n";

    public AuthInterceptor(Context context) {
        this.mContext = context;
    }

    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String time = SystemUtil.getTimer();
        Request.Builder builder = original.newBuilder()
                .addHeader("X-KMS-OS", "Android")
                .addHeader("X-KMS-OSVersion", String.valueOf(Build.VERSION.SDK_INT))
                .addHeader("X-KMS-DevType", SystemUtil.getDevType())
                .addHeader("X-KMS-DeviceId", Utils.getIMEI(mContext))
                .addHeader("X-KMS-Timestamp", time)
                .addHeader("X-KMS-PackageName", SystemUtil.getPackageName(mContext))
                .method(original.method(), original.body());


        Request request = builder.build();

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getCredential());
        strBuilder.append(", ");
        Set<String> params = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        params.addAll(request.url().queryParameterNames());

        strBuilder.append(getSignature(request, time, params));
        String auth = strBuilder.toString();


        builder.addHeader("Authorization", auth);
        request = builder.build();
        return chain.proceed(request);
    }

    private String getCredential() {
        return "Credential=" + BaseActivity.getUserToken();
    }

    private String getSignature(Request request, String timer, Set<String> params) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder
                .append(request.method().toUpperCase())
                .append(SIGNSTRING_LINKER)
                .append(request.url().host().toLowerCase())
                .append(SIGNSTRING_LINKER)
                .append(request.url().encodedPath().toLowerCase())
                .append(SIGNSTRING_LINKER);


        StringBuilder pb = new StringBuilder();
        if (params != null && params.size() > 0) {
            HttpUrl url = request.url();
            for (String param : params) {
                String value = url.queryParameterValues(param).get(0);
                pb
                        .append(param)
                        .append("=")
                        .append(value)
                        .append("&");
            }
            pb.deleteCharAt(pb.length() - 1);
            strBuilder
                    .append(pb.toString())
                    .append(SIGNSTRING_LINKER);
        }
        strBuilder
                .append(timer)
                .append(BaseActivity.getUserSecret());
        return "Signature=" + Utils.computeMD5(strBuilder.toString());
    }
}
