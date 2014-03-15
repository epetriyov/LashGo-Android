package com.check.android.service;

import com.check.model.dto.*;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public interface RestService {
    @POST("/login")
    Response<SessionInfo> login(@Body LoginInfo loginInfo);


    @POST("/register")
    Response<SessionInfo> register(@Body RegisterInfo registerInfo);

    @POST("/social-sign-in")
    Response<SessionInfo> signInWithSocial(@Body SocialInfo socialInfo);
}
