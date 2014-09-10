package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.UserDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 08.09.2014.
 */
public class GetMyUserProfileHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<UserDto> responseObject = service.getMyUserProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USER_PROFILE.name(), responseObject.getResult());
        return bundle;
    }
}
