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

    @POST(Path.Gcm.REGISTER)
    ResponseObject registerDevice(@Body GcmRegistrationDto gcmRegistrationDto);

    @GET(Path.Users.MAIN_SCREEN_INFO)
    ResponseObject<MainScreenInfoDto> getUserMainScreenInfo(@Query("news_last_view") String newsLastView, @Query("subscriptions_last_view") String subscriptionsLastView);

    @GET(Path.Checks.GET)
    ResponseList<CheckDto> getChecks(@Query("search_text") String searchText);

    @Multipart
    @POST(Path.Checks.PHOTOS)
    ResponseObject saveCheckPhoto(@retrofit.http.Path("checkId") int checkId, @Part("photo") TypedFile photo);

    @GET(Path.Checks.VOTE_PHOTOS)
    ResponseList<VotePhoto> getVotePhotos(@retrofit.http.Path("checkId") int checkId);

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

    @PUT(Path.Users.MY_PROFILE)
    ResponseObject saveProfile(@Body UserDto userDto);

    @Multipart
    @POST(Path.Users.AVATAR)
    ResponseObject saveAvatar(@Part("avatar") TypedFile avatar);

    @GET(Path.Checks.COMMENTS)
    ResponseList<CommentDto> getCheckComments(@retrofit.http.Path("checkId") int checkId);

    @GET(Path.Photos.COMMENTS)
    ResponseList<CommentDto> getPhotoComments(@retrofit.http.Path("photoId") long photoId);

    @POST(Path.Checks.COMMENTS)
    ResponseObject<CommentDto> addCheckComment(@retrofit.http.Path("checkId") int checkId, @Body String commentText);

    @POST(Path.Photos.COMMENTS)
    ResponseObject<CommentDto> addPhotoComment(@retrofit.http.Path("photoId") long photoId, @Body String commentText);

    @PUT(Path.Users.RECOVER)
    ResponseObject passwordRecover(@Body String email);

    @GET(Path.Photos.COUNTERS)
    ResponseObject<CheckCounters> getPhotoCounters(@retrofit.http.Path("photoId") long photoId);

    @GET(Path.Checks.COUNTERS)
    ResponseObject<CheckCounters> getCheckCounters(@retrofit.http.Path("checkId") int checkId);

    @POST(Path.Photos.LIKE)
    ResponseObject<Boolean> likePhoto(@Body Long photoId);

    @POST(Path.Users.SUBSCRIPTION_POST)
    ResponseObject subscribe(@Body SubscribeDto subscribeDto);

    @DELETE(Path.Users.SUBSCRIPTION)
    ResponseObject unsubscribe(@retrofit.http.Path("userId") int userId);

    @GET(Path.Users.SUBSCRIPTIONS)
    ResponseList<SubscriptionDto> getSubscriptions(@retrofit.http.Path("userId") int userId);

    @GET(Path.Users.SUBSCRIBERS)
    ResponseList<SubscriptionDto> getSubscribers(@retrofit.http.Path("userId") int userId);

    @GET(Path.Events.GET)
    ResponseList<EventDto> getEvents();

    @GET(Path.Users.GET)
    ResponseList<SubscriptionDto> findUsers(@Query("search_text") String searchText);

    @GET(Path.Checks.USERS)
    ResponseList<SubscriptionDto> getCheckUsers(@retrofit.http.Path("checkId") int checkId);

    @GET(Path.Photos.PHOTO)
    ResponseObject<PhotoDto> getPhoto(@retrofit.http.Path("photoId") long photoId);

    @GET(Path.Photos.VOTES)
    ResponseList<SubscriptionDto> getVoteUsers(@retrofit.http.Path("photoId") long photoId);

    @POST(Path.Photos.COMPLAIN)
    ResponseObject comlainPhoto(@retrofit.http.Path("photoId") long photoId);
}
