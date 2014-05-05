package com.check.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.check.android.service.RestService;
import com.check.android.settings.SettingsHelper;
import com.check.model.dto.Response;
import com.check.model.dto.SessionInfo;
import com.check.model.dto.SocialInfo;
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

    public static final String SOCIAL_DTO = "social_dto";

    public SocialSignInHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        SocialInfo socialInfo = (SocialInfo) intent.getSerializableExtra(SOCIAL_DTO);
        service.signInWithSocial(socialInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SOCIAL_DTO, socialInfo);
        return bundle;
    }
}
