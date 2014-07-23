package com.lashgo.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.service.handlers.*;
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
                            LoginInfo loginInfo = settingsHelper.getLoginInfo();
                            if (loginInfo != null) {
                                login(loginInfo);
                            } else {
                                socialSignIn(settingsHelper.getSocialInfo());
                            }
                        } else {
                            if (expiredActionName != null) {
                                runRequest(expiredActionName, pendingActivities.get(expiredActionName).getExtras());
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
        extras.putSerializable(LoginHandler.LOGIN_DTO, loginInfo);
        runRequest(RestHandlerFactory.ACTION_LOGIN, extras);
    }

    public void register(LoginInfo registerInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(RegisterHandler.REGISTER_DTO, registerInfo);
        runRequest(RestHandlerFactory.ACTION_REGISTER, extras);
    }

    public void socialSignIn(SocialInfo socialInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(SocialSignInHandler.SOCIAL_DTO, socialInfo);
        runRequest(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN, extras);
    }

    public void socialSignUp(ExtendedSocialInfo extendedSocialInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(SocialSignUpHandler.EXTENDED_SOCIAL_DTO, extendedSocialInfo);
        runRequest(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP, extras);
    }

    public void gcmRegisterId(GcmRegistrationDto gcmRegistrationDto) {
        Bundle extras = new Bundle();
        extras.putSerializable(GcmRegisterHandler.GCM_REGISTRATION, gcmRegistrationDto);
        runRequest(RestHandlerFactory.ACTION_GCM_REGISTER_ID, extras);
    }

    public void getMainScreenInfo(String lastNewsViewDate, String lastSubscribtionsViewDate) {
        Bundle extras = new Bundle();
        extras.putString(GetMainScreenHandler.LAST_NEWS_VIEW_DATE, lastNewsViewDate);
        extras.putString(GetMainScreenHandler.LAST_SUBSCRIPTIONS_VIEW_DATE, lastSubscribtionsViewDate);
        runRequest(RestHandlerFactory.ACTION_GET_MAIN_SCREEN_INFO, extras);
    }

    public void getChecks() {
        runRequest(RestHandlerFactory.ACTION_GET_CHECK_LIST, null);
    }
}
