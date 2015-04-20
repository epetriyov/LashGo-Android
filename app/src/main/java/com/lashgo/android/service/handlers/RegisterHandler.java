package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.RegisterResponse;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 27.02.14
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class RegisterHandler extends BaseIntentHandler {

    public RegisterHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        Bundle bundle = new Bundle();
        LoginInfo registerInfo = (LoginInfo) intent.getSerializableExtra(ServiceExtraNames.REGISTER_DTO.name());
        ResponseObject<RegisterResponse> registerResponse = service.register(registerInfo);
        if (registerResponse != null && registerResponse.getResult() != null) {
            settingsHelper.login(registerResponse.getResult().getSessionInfo(),registerInfo);
            bundle.putSerializable(ServiceExtraNames.REGISTER_RESPONSE_INFO.name(), registerResponse.getResult());
        }
        return bundle;
    }
}
