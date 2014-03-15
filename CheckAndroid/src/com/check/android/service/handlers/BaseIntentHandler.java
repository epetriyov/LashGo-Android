package com.check.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.check.android.CheckApplication;
import com.check.android.ForApplication;
import com.check.android.R;
import com.check.android.service.RestService;
import com.check.android.settings.SettingsHelper;
import com.check.model.dto.Response;
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
    RestService service;
    @Inject @ForApplication
    Context context;
    @Inject
    SettingsHelper settingsHelper;

    public BaseIntentHandler() {
        CheckApplication.getInstance().inject(this);
    }

    public final void execute(Intent intent, ResultReceiver callback) {
        String errorMessage = null;
        try {
            Bundle bundle = doExecute(intent);
            if (callback != null) {
                callback.send(SUCCESS_RESPONSE, bundle);
            }
        } catch (RetrofitError e) {
            if (!e.isNetworkError()) {
                try {
                    Response errorResponse = (Response) e.getBodyAs(Response.class);
                    errorMessage = errorResponse.getError().getErrorCode();
                } catch (Exception e1) {
                    errorMessage = context.getString(R.string.server_is_unavailable);
                }
            } else {
                errorMessage = context.getString(R.string.error_no_internet);
            }
        } catch (IOException e) {
            errorMessage = context.getString(R.string.error_no_internet);
        }
        if (errorMessage != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ERROR_EXTRA, errorMessage);
            if (callback != null) {
                callback.send(FAILURE_RESPONSE, bundle);
            }
        }
    }

    protected abstract Bundle doExecute(Intent intent) throws IOException, RetrofitError;

}
