package com.lashgo.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.model.CheckType;
import com.lashgo.model.dto.*;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 25.02.14
 * Time: 23:52
 * To change this template use File | Settings | File Templates.
 */
public class ServiceHelper {

    private Map<String, Intent> pendingActivities = new HashMap<>();

    private Map<String, WeakReference<ServiceCallbackListener>> serviceCallbackListenerMap = new HashMap<>();

    private Context context;

    private static volatile ServiceHelper instance;

    private ServiceHelper(Context context) {
        this.context = context;
    }

    public static ServiceHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (ServiceHelper.class) {
                if (instance == null) {
                    instance = new ServiceHelper(context);
                }
            }
        }
        return instance;
    }

    public void addActionListener(String action, ServiceCallbackListener serviceCallbackListener) {
        serviceCallbackListenerMap.put(action, new WeakReference<>(serviceCallbackListener));
    }

    public void removeActionListener(String action, ServiceCallbackListener serviceCallbackListener) {
        WeakReference<ServiceCallbackListener> listener = serviceCallbackListenerMap.get(action);
        if (listener != null && listener.get() != null && listener.get().equals(serviceCallbackListener)) {
            serviceCallbackListenerMap.remove(action);
        }
    }

    private Intent createIntent(final String actionName) {
        Intent i = new Intent(context, CheckService.class);
        i.setAction(actionName);
        i.putExtra(CheckService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (isPending(actionName)) {
                            pendingActivities.remove(actionName);
                        }
                        WeakReference<ServiceCallbackListener> serviceCallbackListener = serviceCallbackListenerMap.get(actionName);
                        if (serviceCallbackListener != null && serviceCallbackListener.get() != null) {
                            serviceCallbackListener.get().onCommandFinished(actionName, resultCode, resultData);
                        } else {
                            serviceCallbackListenerMap.remove(actionName);
                        }
                    }
//                    }
                }

        );
        return i;
    }

    private boolean isPending(String actionName) {
        return pendingActivities.get(actionName) != null;
    }

    private void runRequest(String actionName, Bundle extras) {
        if (!isPending(actionName)) {
            Intent i = createIntent(actionName);
            if (extras != null) {
                i.putExtras(extras);
            }
            pendingActivities.put(actionName, i);
            WeakReference<ServiceCallbackListener> serviceCallbackListener = serviceCallbackListenerMap.get(actionName);
            if (serviceCallbackListener != null && serviceCallbackListener.get() != null) {
                serviceCallbackListener.get().onCommandStarted();
            } else {
                serviceCallbackListenerMap.remove(actionName);
            }
            context.startService(i);
        }
    }

    public void login(LoginInfo loginInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.LOGIN_DTO.name(), loginInfo);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name(), extras);
    }

    public void register(LoginInfo registerInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_DTO.name(), registerInfo);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name(), extras);
    }

    public void socialSignIn(SocialInfo socialInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.SOCIAL_DTO.name(), socialInfo);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name(), extras);
    }

    public void gcmRegisterId(GcmRegistrationDto gcmRegistrationDto) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.GCM_REGISTRATION.name(), gcmRegistrationDto);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GCM_REGISTER_ID.name(), extras);
    }

    public void sendPhoto(int checkId, String photoPath) {
        Bundle extras = new Bundle();
        extras.putString(BaseIntentHandler.ServiceExtraNames.PHOTO_PATH.name(), photoPath);
        extras.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name(), extras);
    }

    public void getMainScreenInfo(String lastNewsViewDate, String lastSubscribtionsViewDate) {
        Bundle extras = new Bundle();
        extras.putString(BaseIntentHandler.ServiceExtraNames.LAST_NEWS_VIEW_DATE.name(), lastNewsViewDate);
        extras.putString(BaseIntentHandler.ServiceExtraNames.LAST_SUBSCRIPTIONS_VIEW_DATE.name(), lastSubscribtionsViewDate);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name(), extras);
    }

    private void getChecks(String searchText, String checkType, String actionName) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseIntentHandler.ServiceExtraNames.SEARCH_TEXT.name(), searchText);
        bundle.putString(BaseIntentHandler.ServiceExtraNames.CHECK_TYPE.name(), checkType);
        runRequest(actionName, bundle);
    }

    public void getSelfies(String searchText) {
        getChecks(searchText, CheckType.SELFIE.name(), BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_LIST.name());
    }

    public void getActions(String searchText) {
        getChecks(searchText, CheckType.ACTION.name(), BaseIntentHandler.ServiceActionNames.ACTION_GET_ACTIONS_LIST.name());
    }

    public void getVotePhotos(int checkId) {
        Bundle extras = new Bundle();
        extras.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name(), extras);
    }

    public void votePhoto(VoteAction voteAction) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.VOTE_ACTION.name(), voteAction);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name(), extras);
    }

    public void likeCheck(int checkId) {
        Bundle extras = new Bundle();
        extras.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name(), extras);
    }

    public void getUserProfile(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.USER_ID.name(), userId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name(), bundle);
    }

    public void getMyUserProfile() {
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name(), new Bundle());
    }

    public void getCheckPhotos(int checkId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_PHOTOS.name(), bundle);
    }

    public void getUserPhotos(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.USER_ID.name(), userId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name(), bundle);
    }

    public void getMyPhotos() {
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name(), new Bundle());
    }

    public void getCheck(int checkId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name(), bundle);
    }

    public void saveAvatar(String avatarPath) {
        Bundle extras = new Bundle();
        extras.putString(BaseIntentHandler.ServiceExtraNames.AVATAR_PATH.name(), avatarPath);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_AVATAR.name(), extras);
    }

    public void saveProfile(UserDto userDto) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.USER_PROFILE.name(), userDto);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_PROFILE.name(), extras);
    }

    public void getCheckComments(int checkId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_COMMENTS.name(), bundle);
    }

    public void getPhotoComments(long photoId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_COMMENTS.name(), bundle);
    }

    public void addCheckComment(int checkId, String commentText) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        bundle.putString(BaseIntentHandler.ServiceExtraNames.COMMENT_TEXT.name(), commentText);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_ADD_CHECK_COMMENT.name(), bundle);
    }

    public void addPhotoComment(long photoId, String commentText) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        bundle.putString(BaseIntentHandler.ServiceExtraNames.COMMENT_TEXT.name(), commentText);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_ADD_PHOTO_COMMENT.name(), bundle);
    }

    public void recoverPassword(String email) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseIntentHandler.ServiceExtraNames.EMAIL.name(), email);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name(), bundle);
    }

    public void getCheckCounters(int checkId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_COUNTERS.name(), bundle);
    }

    public void getPhotoCounters(long photoId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_COUNTERS.name(), bundle);
    }

    public void likePhoto(long photoId) {
        Bundle extras = new Bundle();
        extras.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name(), extras);
    }

    public void getSubscriptions(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.USER_ID.name(), userId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIPTIONS.name(), bundle);
    }

    public void getSubscribers(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.USER_ID.name(), userId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIBERS.name(), bundle);
    }

    public void subscribe(SubscribeDto subscribeDto) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.SUBSCRIPTION_DTO.name(), subscribeDto);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name(), extras);
    }

    public void unsubscribe(int userId) {
        Bundle extras = new Bundle();
        extras.putInt(BaseIntentHandler.ServiceExtraNames.USER_ID.name(), userId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name(), extras);
    }

    public void getEvents(boolean subscriptions) {
        Bundle extras = new Bundle();
        extras.putBoolean(BaseIntentHandler.ServiceExtraNames.SUBSCRIPTION_EVENTS.name(), subscriptions);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_EVENTS.name(), extras);
    }

    public void searchUsers(String searchText) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseIntentHandler.ServiceExtraNames.SEARCH_TEXT.name(), searchText);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_FIND_USERS.name(), bundle);
    }

    public void getCheckUsers(int checkId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseIntentHandler.ServiceExtraNames.CHECK_ID.name(), checkId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_USERS.name(), bundle);
    }

    public void getPhoto(long photoId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO.name(), bundle);
    }

    public void getVoteUsers(long photoId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_USERS.name(), bundle);
    }

    public void complainPhoto(long photoId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseIntentHandler.ServiceExtraNames.PHOTO_ID.name(), photoId);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_COMPLAIN_PHOTO.name(), bundle);
    }
}
