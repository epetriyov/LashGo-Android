package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.LoginInfo;
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
        LoginInfo registerInfo = (LoginInfo) intent.getSerializableExtra(REGISTER_DTO);
        service.register(registerInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(REGISTER_DTO, registerInfo);
        return bundle;
    }
}
