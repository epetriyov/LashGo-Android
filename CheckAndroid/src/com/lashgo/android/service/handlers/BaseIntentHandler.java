package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.R;
import com.lashgo.android.service.transport.RestService;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.utils.NetworkUtils;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import javax.inject.Inject;
import java.io.IOException;

/**
 * User: eugene.petriyov
 * Date: 25.06.13
 * Time: 13:40
 */

/**
 * base intent executer
 */
public abstract class BaseIntentHandler {

    public static BaseIntentHandler getIntentHandler(String action) {
        if (ServiceActionNames.ACTION_LOGIN.name().equals(action)) {
            return new LoginHandler();
        } else if (ServiceActionNames.ACTION_REGISTER.name().equals(action)) {
            return new RegisterHandler();
        } else if (ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name().equals(action)) {
            return new SocialSignInHandler();
        } else if (ServiceActionNames.ACTION_GCM_REGISTER_ID.name().equals(action)) {
            return new GcmRegisterHandler();
        } else if (ServiceActionNames.ACTION_CONFIRM_SOCIAL_SIGN_UP.name().equals(action)) {
            return new SocialSignUpHandler();
        } else if (ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name().equals(action)) {
            return new GetMainScreenHandler();
        } else if (ServiceActionNames.ACTION_GET_CHECK_LIST.name().equals(action)) {
            return new GetCheckListHandler();
        } else {
            throw new IllegalArgumentException("illegal action - " + action);
        }
    }

    public static enum ServiceActionNames {
        ACTION_LOGIN,
        ACTION_REGISTER, ACTION_SOCIAL_SIGN_IN,
        ACTION_GCM_REGISTER_ID, ACTION_PASSWORD_RECOVER,
        ACTION_CONFIRM_SOCIAL_SIGN_UP, ACTION_GET_MAIN_SCREEN_INFO,
        ACTION_GET_CHECK_LIST
    }

    public static enum ServiceExtraNames {
        GCM_REGISTRATION,
        KEY_CHECK_DTO_LIST, LAST_NEWS_VIEW_DATE,
        LAST_SUBSCRIPTIONS_VIEW_DATE, MAIN_SCREEN_INFO,
        LOGIN_DTO, SESSION_INFO, REGISTER_DTO, SOCIAL_DTO,
        EXTENDED_SOCIAL_DTO
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

    protected abstract Bundle doExecute(Intent intent) throws IOException, RetrofitError;

}
