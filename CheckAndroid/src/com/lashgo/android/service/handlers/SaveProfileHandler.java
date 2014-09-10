package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 10.09.2014.
 */
public class SaveProfileHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.saveProfile((com.lashgo.model.dto.UserDto) intent.getSerializableExtra(ServiceExtraNames.USER_PROFILE.name()));
        return intent.getExtras();
    }
}
