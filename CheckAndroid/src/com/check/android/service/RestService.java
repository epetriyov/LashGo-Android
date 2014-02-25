package com.check.android.service;

import com.check.model.dto.LoginInfo;
import com.check.model.dto.SessionInfo;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public interface RestService {
    @POST("/login")
    SessionInfo login(@Header("uuid") String uuid, @Body LoginInfo loginInfo);


    @POST("/register")
    SessionInfo register(@Header("uuid") String uuid, @Body LoginInfo loginInfo);
}
