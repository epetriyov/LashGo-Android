package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ExtendedSocialInfo;
import com.lashgo.model.dto.RegisterResponse;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.SessionInfo;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 24.06.2014.
 */
public class SocialSignUpHandler extends BaseIntentHandler {

    public SocialSignUpHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        Bundle bundle = new Bundle();
        ExtendedSocialInfo extendedSocialInfo = (ExtendedSocialInfo) intent.getSerializableExtra(ServiceExtraNames.EXTENDED_SOCIAL_DTO.name());
        ResponseObject<RegisterResponse> registerResponse = service.confirmSocialSignUp(extendedSocialInfo);
        if (registerResponse != null && registerResponse.getResult() != null) {
            settingsHelper.socialLogin(new SessionInfo(registerResponse.getResult().getSessionId(), registerResponse.getResult().getUserDto() != null ? registerResponse.getResult().getUserDto().getId() : -1), extendedSocialInfo);
            bundle.putSerializable(ServiceExtraNames.REGISTER_RESPONSE_INFO.name(), registerResponse.getResult());
        }
        return bundle;
    }
}
