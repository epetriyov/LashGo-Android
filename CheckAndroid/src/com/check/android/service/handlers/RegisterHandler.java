package com.check.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.check.android.service.RestService;
import com.check.android.settings.SettingsHelper;
import com.check.model.dto.RegisterInfo;
import com.check.model.dto.Response;
import com.check.model.dto.SessionInfo;
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

    public static final String REGISTER_DTO = "register_dto";

    public RegisterHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        RegisterInfo registerInfo = (RegisterInfo) intent.getSerializableExtra(REGISTER_DTO);
        service.register(registerInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(REGISTER_DTO, registerInfo);
        return bundle;
    }
}
