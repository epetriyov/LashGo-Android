package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    public LoginHandler(Context context) {
        super(context);
    }

    @Override
    public Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        Bundle bundle = intent.getExtras();
        LoginInfo loginInfo = (LoginInfo) intent.getSerializableExtra(ServiceExtraNames.LOGIN_DTO.name());
        ResponseObject<SessionInfo> sessionInfo = service.login(loginInfo);
        if (sessionInfo != null && sessionInfo.getResult() != null) {
            settingsHelper.login(sessionInfo.getResult(), loginInfo);
            bundle.putSerializable(ServiceExtraNames.SESSION_INFO.name(), sessionInfo);
        }
        return bundle;
    }
}
