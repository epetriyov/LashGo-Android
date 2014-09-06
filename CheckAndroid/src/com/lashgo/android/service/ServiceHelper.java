package com.lashgo.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.model.ErrorCodes;
import com.lashgo.model.dto.*;

import javax.inject.Inject;
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

    private SettingsHelper settingsHelper;

    private Map<String, Intent> pendingActivities = new HashMap<>();

    private Map<String, WeakReference<ServiceCallbackListener>> serviceCallbackListenerMap = new HashMap<>();

    private Context context;

    private String expiredActionName;
    private Bundle expiredExtras;

    @Inject
    public ServiceHelper(Context context, SettingsHelper settingsHelper) {
        this.context = context;
        this.settingsHelper = settingsHelper;
        LashgoApplication.getInstance().inject(this);
    }

    public void addActionListener(String action, ServiceCallbackListener serviceCallbackListener) {
        serviceCallbackListenerMap.put(action, new WeakReference<>(serviceCallbackListener));
    }

    public void removeActionListener(String action) {
        serviceCallbackListenerMap.remove(action);
    }

    private Intent createIntent(final String actionName) {
        Intent i = new Intent(context, CheckService.class);
        i.setAction(actionName);
        i.putExtra(CheckService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        ErrorDto errorDto = (ErrorDto) resultData.getSerializable(BaseIntentHandler.ERROR_EXTRA);
                        if (errorDto != null && ErrorCodes.SESSION_EXPIRED.equals(errorDto.getErrorCode())) {
                            /**
                             * handle session expiration
                             */
                            expiredActionName = actionName;
                            Intent expiredIntent = pendingActivities.get(expiredActionName);
                            if (expiredIntent != null) {
                                expiredExtras = expiredIntent.getExtras();
                            }
                            if (isPending(actionName)) {
                                pendingActivities.remove(actionName);
                            }
                            LoginInfo loginInfo = settingsHelper.getLoginInfo();
                            if (loginInfo != null) {
                                login(loginInfo);
                            } else {
                                socialSignIn(settingsHelper.getSocialInfo());
                            }
                        } else {
                            if (expiredActionName != null) {
                                runRequest(expiredActionName, expiredExtras);
                                expiredActionName = null;
                            }
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
                    }
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

    public void socialSignUp(ExtendedSocialInfo extendedSocialInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(BaseIntentHandler.ServiceExtraNames.EXTENDED_SOCIAL_DTO.name(), extendedSocialInfo);
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_CONFIRM_SOCIAL_SIGN_UP.name(), extras);
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

    public void getChecks() {
        runRequest(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_LIST.name(), new Bundle());
    }

    public void getVotePhotos(int checkId, boolean isPhotosCountedIncluded) {
        Bundle extras = new Bundle();
        extras.putBoolean(BaseIntentHandler.ServiceExtraNames.IS_PHOTOS_COUNT_INCLUDED.name(), isPhotosCountedIncluded);
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
}
