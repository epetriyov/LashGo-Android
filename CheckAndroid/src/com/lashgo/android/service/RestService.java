package com.lashgo.android.service;

import com.lashgo.model.Path;
import com.lashgo.model.dto.*;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public interface RestService {
    @POST(Path.Users.LOGIN)
    ResponseObject<SessionInfo> login(@Body LoginInfo loginInfo);

    @POST(Path.Users.REGISTER)
    ResponseObject register(@Body LoginInfo registerInfo);

    @POST(Path.Users.SOCIAL_SIGN_IN)
    ResponseObject<SessionInfo> socialSignIn(@Body SocialInfo socialInfo);

    @POST(Path.Users.SOCIAL_SIGN_UP)
    ResponseObject<SessionInfo> confirmSocialSignUp(@Body ExtendedSocialInfo socialInfo);

    @POST(Path.Gcm.REGISTER)
    ResponseObject registerDevice(@Body GcmRegistrationDto gcmRegistrationDto);

    @GET(Path.Checks.CURRENT)
    CheckDto getLastCheck();
}
