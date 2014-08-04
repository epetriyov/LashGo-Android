package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ExtendedSocialInfo;
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
        ExtendedSocialInfo extendedSocialInfo = (ExtendedSocialInfo) intent.getSerializableExtra(ServiceExtraNames.EXTENDED_SOCIAL_DTO.name());
        ResponseObject<SessionInfo> sessionInfo = service.confirmSocialSignUp(extendedSocialInfo);
        settingsHelper.socialLogin(sessionInfo.getResult(), extendedSocialInfo);
        return intent.getExtras();
    }
}
