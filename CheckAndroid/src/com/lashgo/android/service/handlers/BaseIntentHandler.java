package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.R;
import com.lashgo.android.service.RestService;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.utils.ContextUtils;
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
        ErrorDto errorDto = null;
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
