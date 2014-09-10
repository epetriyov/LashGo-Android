package com.lashgo.android.service.transport;

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
    ResponseObject saveCheckPhoto(@retrofit.http.Path("checkId") int checkId, @Part("photo") TypedFile photo);

    @GET(Path.Checks.VOTE_PHOTOS)
    ResponseObject<VotePhotosResult> getVotePhotos(@retrofit.http.Path("checkId") int checkId, @Query("is_count_included") boolean isCountIncluded);

    @POST(Path.Photos.VOTE)
    ResponseObject votePhoto(@Body VoteAction voteAction);

    @POST(Path.Checks.LIKE)
    ResponseObject<Boolean> likeCheck(@Body Integer checkId);

    @GET(Path.Users.PROFILE)
    ResponseObject<UserDto> getUserProfile(@retrofit.http.Path("userId") int userId);

    @GET(Path.Users.MY_PROFILE)
    ResponseObject<UserDto> getMyUserProfile();

    @GET(Path.Checks.PHOTOS)
    ResponseList<PhotoDto> getCheckPhotos(@retrofit.http.Path("checkId") int checkId);

    @GET(Path.Users.MY_PHOTOS)
    ResponseList<PhotoDto> getMyPhotos();

    @GET(Path.Users.PHOTOS)
    ResponseList<PhotoDto> getUserPhotos(@retrofit.http.Path("userId") int userId);

    @GET(Path.Checks.CHECK)
    ResponseObject<CheckDto> getCheck(@retrofit.http.Path("checkId") int checkId);

    @PUT(Path.Users.PROFILE)
    ResponseObject saveProfile(@Body UserDto userDto);

    @POST(Path.Users.AVATAR)
    ResponseObject saveAvatar(@Part("avatar") TypedFile avatar);
}
