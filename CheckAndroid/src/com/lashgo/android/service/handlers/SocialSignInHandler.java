package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.SessionInfo;
import com.lashgo.model.dto.SocialInfo;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 27.02.14
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class SocialSignInHandler extends BaseIntentHandler {

    public SocialSignInHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        SocialInfo socialInfo = (SocialInfo) intent.getSerializableExtra(ServiceExtraNames.SOCIAL_DTO.name());
        ResponseObject<SessionInfo> sessionInfo = service.socialSignIn(socialInfo);
        settingsHelper.socialLogin(sessionInfo.getResult(), socialInfo);
        return intent.getExtras();
    }
}
