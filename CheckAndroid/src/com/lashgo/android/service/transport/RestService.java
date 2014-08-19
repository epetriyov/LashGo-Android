package com.lashgo.android.service.transport;

import com.lashgo.model.CheckApiHeaders;
import com.lashgo.model.Path;
import com.lashgo.model.dto.*;
import retrofit.http.*;
import retrofit.mime.TypedFile;

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
    ResponseObject<RegisterResponse> register(@Body LoginInfo registerInfo);

    @POST(Path.Users.SOCIAL_SIGN_IN)
    ResponseObject<RegisterResponse> socialSignIn(@Body SocialInfo socialInfo);

    @POST(Path.Users.SOCIAL_SIGN_UP)
    ResponseObject<RegisterResponse> confirmSocialSignUp(@Body ExtendedSocialInfo socialInfo);

    @POST(Path.Gcm.REGISTER)
    ResponseObject registerDevice(@Body GcmRegistrationDto gcmRegistrationDto);

    @GET(Path.Users.MAIN_SCREEN_INFO)
    ResponseObject<MainScreenInfoDto> getUserMainScreenInfo(@Query("news_last_view") String newsLastView, @Query("subscriptions_last_view") String subscriptionsLastView);

    @GET(Path.Checks.GET)
    ResponseList<CheckDto> getChecks();

    @Multipart
    @POST(Path.Checks.PHOTOS)
    ResponseObject saveCheckPhoto(@Header(CheckApiHeaders.SESSION_ID) String sessionId, @retrofit.http.Path("checkId") long checkId, @Part("photo") TypedFile photo);
}
