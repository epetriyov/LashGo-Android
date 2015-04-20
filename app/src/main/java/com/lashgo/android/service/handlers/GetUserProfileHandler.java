package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.UserDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 08.09.2014.
 */
public class GetUserProfileHandler extends BaseIntentHandler {
    public GetUserProfileHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<UserDto> response = service.getUserProfile(intent.getIntExtra(ServiceExtraNames.USER_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USER_PROFILE.name(), response.getResult());
        return bundle;
    }
}
