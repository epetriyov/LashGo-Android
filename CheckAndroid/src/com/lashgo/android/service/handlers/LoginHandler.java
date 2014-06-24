package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.SessionInfo;
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

    public LoginHandler() {
        super();
    }

    @Override
    public Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        LoginInfo loginInfo = (LoginInfo) intent.getSerializableExtra(LOGIN_DTO);
        ResponseObject<SessionInfo> sessionInfo = service.login(loginInfo);
        settingsHelper.login(sessionInfo.getResult(), loginInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SESSION_INFO, sessionInfo);
        bundle.putSerializable(LOGIN_DTO, loginInfo);
        return bundle;
    }
}
