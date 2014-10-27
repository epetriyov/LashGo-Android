package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.R;
import com.lashgo.android.service.transport.RestService;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.utils.NetworkUtils;
import com.lashgo.model.ErrorCodes;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: eugene.petriyov
 * Date: 25.06.13
 * Time: 13:40
 */

/**
 * base intent executer
 */
public abstract class BaseIntentHandler {

    private Map<String, String> messagesMap;

    public static BaseIntentHandler getIntentHandler(String action) {
        if (ServiceActionNames.ACTION_LOGIN.name().equals(action)) {
            return new LoginHandler();
        } else if (ServiceActionNames.ACTION_REGISTER.name().equals(action)) {
            return new RegisterHandler();
        } else if (ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name().equals(action)) {
            return new SocialSignInHandler();
        } else if (ServiceActionNames.ACTION_GCM_REGISTER_ID.name().equals(action)) {
            return new GcmRegisterHandler();
        } else if (ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name().equals(action)) {
            return new GetMainScreenHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK_LIST.name().equals(action)) {
            return new GetCheckListHandler();
        } else if (ServiceActionNames.ACTION_SEND_PHOTO.name().equals(action)) {
            return new SendPhotoHandler();
        } else if (ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name().equals(action)) {
            return new GetVotePhotosHandler();
        } else if (ServiceActionNames.ACTION_VOTE.name().equals(action)) {
            return new VoteHandler();
        } else if (ServiceActionNames.ACTION_LIKE_CHECK.name().equals(action)) {
            return new CheckLikeHandler();
        } else if (ServiceActionNames.ACTION_LIKE_PHOTO.name().equals(action)) {
            return new PhotoLikeHandler();
        } else if (ServiceActionNames.ACTION_GET_USER_PROFILE.name().equals(action)) {
            return new GetUserProfileHandler();
        } else if (ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name().equals(action)) {
            return new GetMyUserProfileHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK_PHOTOS.name().equals(action)) {
            return new GetCheckPhotosHandler();
        } else if (ServiceActionNames.ACTION_GET_USER_PHOTOS.name().equals(action)) {
            return new GetUserPhotosHandler();
        } else if (ServiceActionNames.ACTION_GET_MY_PHOTOS.name().equals(action)) {
            return new GetMyPhotosHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK.name().equals(action)) {
            return new GetCheckHandler();
        } else if (ServiceActionNames.ACTION_SAVE_AVATAR.name().equals(action)) {
            return new SaveAvatarHandler();
        } else if (ServiceActionNames.ACTION_SAVE_PROFILE.name().equals(action)) {
            return new SaveProfileHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK_COMMENTS.name().equals(action)) {
            return new GetCheckCommentsHandler();
        } else if (ServiceActionNames.ACTION_GET_PHOTO_COMMENTS.name().equals(action)) {
            return new GetPhotoCommentsHandler();
        } else if (ServiceActionNames.ACTION_ADD_CHECK_COMMENT.name().equals(action)) {
            return new AddCheckCommentHandler();
        } else if (ServiceActionNames.ACTION_ADD_PHOTO_COMMENT.name().equals(action)) {
            return new AddPhotoCommentHandler();
        } else if (ServiceActionNames.ACTION_PASSWORD_RECOVER.name().equals(action)) {
            return new PasswordRecoverHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK_COUNTERS.name().equals(action)) {
            return new GetCheckCountersHandler();
        } else if (ServiceActionNames.ACTION_GET_PHOTO_COUNTERS.name().equals(action)) {
            return new GetPhotoCountersHandler();
        } else if (ServiceActionNames.ACTION_GET_SUBSCRIPTIONS.name().equals(action)) {
            return new GetSubscrptionsHandler();
        } else if (ServiceActionNames.ACTION_GET_SUBSCRIBERS.name().equals(action)) {
            return new GetSubscribersHandler();
        } else if (ServiceActionNames.ACTION_SUBSCRIBE.name().equals(action)) {
            return new SubscribeHandler();
        } else if (ServiceActionNames.ACTION_UNSUBSCRIBE.name().equals(action)) {
            return new UnsubscribeHandler();
        } else if (ServiceActionNames.ACTION_GET_EVENTS.name().equals(action)) {
            return new GetEventsHandler();
        }  else if (ServiceActionNames.ACTION_FIND_USERS.name().equals(action)) {
            return new FindUserHandler();
        }  else if (ServiceActionNames.ACTION_GET_CHECK_USERS.name().equals(action)) {
            return new GetCheckUsersHandler();
        }  else if (ServiceActionNames.ACTION_GET_PHOTO.name().equals(action)) {
            return new GetPhotoHandler();
        } else {
            throw new IllegalArgumentException("illegal action - " + action);
        }
    }

    public static enum ServiceActionNames {
        ACTION_LOGIN,
        ACTION_REGISTER, ACTION_SOCIAL_SIGN_IN,
        ACTION_GCM_REGISTER_ID, ACTION_PASSWORD_RECOVER,
        ACTION_GET_MAIN_SCREEN_INFO,
        ACTION_SEND_PHOTO, ACTION_GET_VOTE_PHOTOS, ACTION_VOTE,
        ACTION_LIKE_CHECK, ACTION_GET_USER_PROFILE,
        ACTION_GET_MY_USER_PROFILE, ACTION_GET_CHECK_PHOTOS,
        ACTION_GET_USER_PHOTOS, ACTION_GET_MY_PHOTOS,
        ACTION_GET_CHECK, ACTION_SAVE_AVATAR,
        ACTION_SAVE_PROFILE, ACTION_GET_CHECK_COMMENTS,
        ACTION_GET_PHOTO_COMMENTS, ACTION_ADD_PHOTO_COMMENT,
        ACTION_ADD_CHECK_COMMENT, ACTION_GET_CHECK_COUNTERS,
        ACTION_GET_PHOTO_COUNTERS, ACTION_LIKE_PHOTO,
        ACTION_GET_SUBSCRIPTIONS, ACTION_GET_SUBSCRIBERS,
        ACTION_SUBSCRIBE, ACTION_UNSUBSCRIBE, ACTION_GET_EVENTS,
        ACTION_FIND_USERS, ACTION_GET_CHECK_USERS, ACTION_GET_PHOTO, ACTION_GET_CHECK_LIST
    }

    public static enum ServiceExtraNames {
        GCM_REGISTRATION,
        KEY_CHECK_DTO_LIST, LAST_NEWS_VIEW_DATE,
        LAST_SUBSCRIPTIONS_VIEW_DATE, MAIN_SCREEN_INFO,
        LOGIN_DTO, SESSION_INFO, REGISTER_DTO, SOCIAL_DTO,
        EXTENDED_SOCIAL_DTO, PHOTO_PATH, REGISTER_RESPONSE_INFO,
        VOTE_PHOTO_LIST, VOTE_ACTION, CHECK_ID, IS_LIKE_ADDED,
        USER_ID, USER_PROFILE, PHOTOS_LIST, CHECK_DTO,
        AVATAR_PATH, PHOTO_ID, COMMENTS_LIST, COMMENT_TEXT,
        COMMENT, EMAIL, COUNTERS, SUBSCRIPTION_DTO, SUBSCRIPTIONS_DTO,
        SUBSCRIBERS_DTO, EVENTS_DTO, SEARCH_TEXT, USERS_DTO,
        PHOTO_DTO, IS_PHOTOS_COUNT_INCLUDED
    }

    public static final String ERROR_EXTRA = "error_extra";
    public static final int SUCCESS_RESPONSE = 1;
    public static final int FAILURE_RESPONSE = 2;
    @Inject
    protected RestService service;
    @Inject
    protected Context context;
    @Inject
    protected Handler handler;
    @Inject
    protected SettingsHelper settingsHelper;

    public BaseIntentHandler() {
        LashgoApplication.getInstance().inject(this);
        initMessagesMap();

    }

    public BaseIntentHandler(Context context, Handler handler, SettingsHelper settingsHelper, RestService service) {
        this.context = context;
        this.handler = handler;
        this.settingsHelper = settingsHelper;
        this.service = service;
        LashgoApplication.getInstance().inject(this);
    }

    public final void execute(Intent intent, ResultReceiver callback) {
        ErrorDto errorDto;
        try {
            if (NetworkUtils.isNetAvailable(context)) {
                Bundle bundle = doExecute(intent);
                if (callback != null) {
                    callback.send(SUCCESS_RESPONSE, bundle);
                }
                return;
            } else {
                throw new IOException();
            }
        } catch (RetrofitError e) {
            if (!e.isNetworkError()) {
                try {
                    errorDto = ((ResponseObject) e.getBodyAs(ResponseObject.class)).getError();
                    bindMessage(errorDto);
                } catch (Exception e1) {
                    errorDto = new ErrorDto("", context.getString(R.string.server_is_unavailable));
                }
            } else {
                errorDto = new ErrorDto("", context.getString(R.string.error_no_internet));
            }
        } catch (IOException e) {
            errorDto = new ErrorDto("", context.getString(R.string.error_no_internet));
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ERROR_EXTRA, errorDto);
        if (callback != null) {
            callback.send(FAILURE_RESPONSE, bundle);
        }
    }

    private void bindMessage(ErrorDto errorDto) {
        if (errorDto != null && !TextUtils.isEmpty(errorDto.getErrorCode())) {
            errorDto.setErrorMessage(messagesMap.get(errorDto.getErrorCode()));
        }
    }

    private void initMessagesMap() {
        messagesMap = new HashMap<>();
        messagesMap.put(ErrorCodes.USER_NOT_EXISTS, context.getString(R.string.user_not_found));
        messagesMap.put(ErrorCodes.USER_ALREADY_EXISTS, context.getString(R.string.user_already_exist));
        messagesMap.put(ErrorCodes.UUID_IS_EMPTY, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.INVALID_CLIENT_TYPE, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.REGISTRATION_ID_IS_EMPTY, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.SESSION_IS_EMPTY, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.SESSION_EXPIRED, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.WRONG_SESSION, context.getString(R.string.send_invalid_data));

        messagesMap.put(ErrorCodes.PHOTO_ALREADY_EXISTS, context.getString(R.string.photo_alredy_exist));
        messagesMap.put(ErrorCodes.PHOTO_READ_ERROR, context.getString(R.string.server_error));
        messagesMap.put(ErrorCodes.PHOTO_WRITE_ERROR, context.getString(R.string.server_error));
        messagesMap.put(ErrorCodes.UNSUPPORTED_SOCIAL, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.INTERNAL_SERVER_ERROR, context.getString(R.string.server_error));
        messagesMap.put(ErrorCodes.CHECK_ID_NULL, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.USERS_DOESNT_MATCHES, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.EMPTY_EMAIL, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.SOCIAL_WRONG_DATA, context.getString(R.string.send_invalid_data));
        messagesMap.put(ErrorCodes.USER_WITH_LOGIN_ALREADY_EXISTS, context.getString(R.string.user_already_exist));
        messagesMap.put(ErrorCodes.USER_WITH_EMAIL_ALREADY_EXISTS, context.getString(R.string.email_already_exist));
        messagesMap.put(ErrorCodes.PHOTO_ID_NULL, context.getString(R.string.send_invalid_data));
    }


    protected abstract Bundle doExecute(Intent intent) throws IOException, RetrofitError;

}
