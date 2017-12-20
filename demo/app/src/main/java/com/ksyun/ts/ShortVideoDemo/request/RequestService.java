package com.ksyun.ts.ShortVideoDemo.request;

import com.ksyun.ts.ShortVideoDemo.model.User;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xiaoqiang on 2017/10/27.
 */

interface RequestService {
    @GET("api/v1/mobile/code/send")
    Observable<RequestModel> getPhoneVerification(@Query("deviceId") String deviceId,
                                                  @Query("mobile") String mobile);

    @POST("/api/v1/mobile/code/check")
    Observable<RequestModel<User>> login(@Body RequestBody body);

    @GET("api/v1/logout")
    Observable<RequestModel> logout();


    @POST("api/v1/user/info/update")
    Observable<RequestModel<User>> setUserInfo(@Body RequestBody body);


    @GET("api/v1/user/info")
    Observable<RequestModel<User>> getUserInfo();


}
