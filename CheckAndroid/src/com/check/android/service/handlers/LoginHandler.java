package com.check.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.check.android.service.RestService;
import com.check.android.settings.SettingsHelper;
import com.check.model.dto.LoginInfo;
import com.check.model.dto.Response;
import com.check.model.dto.SessionInfo;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * User: eugene.petriyov
 * Date: 25.06.13
 * Time: 13:42
 */
public class LoginHandler extends BaseIntentHandler {

    public static final String LOGIN_DTO = "login_dto";

    public static final String SESSION_INFO = "session_info";

    public LoginHandler(Context context, RestService service) {
        super(context, service);
    }

    @Override
    public Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        LoginInfo loginInfo = (LoginInfo) intent.getSerializableExtra(LOGIN_DTO);
        Response<SessionInfo> sessionInfo = service.login(loginInfo);
        Log.d("SessionInfo", sessionInfo.getResult().getSessionId());
        SettingsHelper.getInstance(context).login(sessionInfo.getResult(), loginInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SESSION_INFO, sessionInfo);
        return bundle;
    }
}
