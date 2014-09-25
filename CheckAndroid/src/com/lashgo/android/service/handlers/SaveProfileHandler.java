package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.UserDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 10.09.2014.
 */
public class SaveProfileHandler extends BaseIntentHandler {
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