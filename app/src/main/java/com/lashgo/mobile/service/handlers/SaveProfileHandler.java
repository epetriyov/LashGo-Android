package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.UserDto;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 10.09.2014.
 */
public class SaveProfileHandler extends BaseIntentHandler {
    public SaveProfileHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        UserDto userDto = (com.lashgo.model.dto.UserDto) intent.getSerializableExtra(ServiceExtraNames.USER_PROFILE.name());
        service.saveProfile(userDto);
        if (settingsHelper.getLoginInfo() != null) {
            settingsHelper.saveLoginInfo(new LoginInfo(userDto.getLogin(), userDto.getPasswordHash()));
        }
        return intent.getExtras();
    }
}
