package com.ksyun.ts.ShortVideoDemo.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ksyun.ts.ShortVideoDemo.auth.AuthInterceptor;
import com.ksyun.ts.ShortVideoDemo.model.ErrorModel;
import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.ui.Utils;
import com.ksyun.ts.shortvideo.KSVSError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiaoqiang on 2017/10/27.
 */

public class RequestManager {
    private final static String BASE_URL = "https://kms-svsdk-demo-api.ksyun.com/";
    private Retrofit mRetrofit;
    private RequestService mRequestService;
    private List<Disposable> mDisposables = new ArrayList<Disposable>();
    private Context mContext;

    public RequestManager(Context context) {
        this.mContext = context;
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(new AuthInterceptor(context))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build();
        mRequestService = mRetrofit.create(RequestService.class);
    }


    public void getPhoneVerification(String mobile, IKSVSRequestListener<RequestModel> listener) {
        mRequestService.getPhoneVerification(Utils.getIMEI(mContext), mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestObserver(listener));
    }

    public void login(String mobile, String code, IKSVSRequestListener<User> listener) {
        RequestBody body = new BodyBuild()
                .bindDeviceID(Utils.getIMEI(mContext))
                .bindCode(code)
                .bindMobile(mobile)
                .build();
        mRequestService.login(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestObserver(listener));
    }

    public void logout(IKSVSRequestListener<RequestModel> listener) {
        mRequestService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestObserver(listener));
    }

    public void getUserInfo(IKSVSRequestListener<User> listener) {
        mRequestService.getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestObserver(listener));
    }

    public void setUserInfo(String headUrlPath, String name, boolean gender,
                            IKSVSRequestListener<User> listener) {
        if (TextUtils.isEmpty(name)) {
            name = "大西瓜";
        }
        RequestBody body = new BodyBuild()
                .bindHeadUrlPath(headUrlPath)
                .bindNickname(name)
                .bindGender(gender)
                .build();
        mRequestService.setUserInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RequestObserver(listener));
    }


    public void releaseAllReuqest() {
        for (Disposable disposable : mDisposables) {
            disposable.dispose();
        }
        mDisposables.clear();
    }

    /*****数据解析*****/

    /**
     * 网络访问成功。需要检查数据是否合法
     *
     * @param listener 回调
     * @param value    网络请求结果
     */
    private void onParseData(IKSVSRequestListener listener, RequestModel value) {

        if (value == null) {
            ErrorModel info = new ErrorModel();
            info.setCode(String.valueOf(KSVSError.KSVS_REQUEST_ERROR_RESPONSE_NULL));
            info.setMessage("Response null");
            onParseError(listener, info);
        } else if (value.getError() != null) {// 这种情况一般不会出现。当有error时，HTTP的状态码都是400以上
            onParseError(listener, value.getError());
        } else {
            listener.onSuccess(value.getData());
        }
    }


    private void onParseError(IKSVSRequestListener listener, ErrorModel info) {
        Log.e(RequestManager.class.getName(), "网络请求失败,错误码:" +
                info.getCode() + ",错误原因：" + info.getMessage());
        listener.onFailed(info);
    }

    private void onParseError(IKSVSRequestListener listener, ResponseBody errorBody) {
        if (listener == null) return;
        ErrorModel info = null;
        try {
            String str = errorBody.string();
            RequestModel data = new Gson().fromJson(str, RequestModel.class);
            info = data.getError();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JsonSyntaxException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (info == null) {
            info = new ErrorModel();
            info.setCode(String.valueOf(KSVSError.KSVS_NETWORK_OTHER_ERROR));
            info.setMessage("error body is null");
        }
        onParseError(listener, info);
    }

    private void onParseError(IKSVSRequestListener listener, Throwable e) {
        if (listener == null) return;
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            if (exception.response() == null) {
                ErrorModel info = new ErrorModel();
                info.setCode(String.valueOf(KSVSError.KSVS_REQUEST_ERROR_RESPONSE_NULL));
                info.setMessage("request error ,Exception response   null");
                onParseError(listener, info);
            } else {
                onParseError(listener, exception.response().errorBody());
            }
        } else {
            e.printStackTrace();
            ErrorModel info = new ErrorModel();
            info.setCode(String.valueOf(KSVSError.KSVS_REQUEST_ERROR_RESPONSE_NULL));
            info.setMessage("request error ,Exception response   null");
            onParseError(listener, info);
        }
    }

    public interface IKSVSRequestListener<T> {
        void onSuccess(T t);

        void onFailed(ErrorModel errorInfo);
    }

    /**
     * 网络请求默认解析类型
     */
    class RequestObserver implements Observer<RequestModel> {
        protected IKSVSRequestListener mListener;
        private Disposable mDisposable;

        RequestObserver(IKSVSRequestListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onSubscribe(Disposable d) {
            mDisposable = d;
            mDisposables.add(mDisposable);
        }

        @Override
        public void onNext(RequestModel value) {
            onParseData(mListener, value);
        }

        @Override
        public void onError(Throwable e) {
            onParseError(mListener, e);
            mDisposable.dispose();
            mDisposables.remove(mDisposable);
        }

        @Override
        public void onComplete() {
            mDisposable.dispose();
            mDisposables.remove(mDisposable);
        }
    }
}
