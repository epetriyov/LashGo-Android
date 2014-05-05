package com.check.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.check.android.service.handlers.*;
import com.check.model.dto.*;

import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.*;

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

    public ServiceHelper(Context context) {
        this.context = context;
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

    public void register(RegisterInfo registerInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(RegisterHandler.REGISTER_DTO, registerInfo);
        runRequest(RestHandlerFactory.ACTION_REGISTER, extras);
    }

    public void socialSignIn(SocialInfo socialInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(SocialSignInHandler.SOCIAL_DTO, socialInfo);
        runRequest(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN, extras);
    }

    public void gcmRegisterId(GcmRegistrationDto gcmRegistrationDto) {
        Bundle extras = new Bundle();
        extras.putSerializable(GcmRegisterHandler.GCM_REGISTRATION, gcmRegistrationDto);
        runRequest(RestHandlerFactory.ACTION_GCM_REGISTER_ID, extras);
    }
}
