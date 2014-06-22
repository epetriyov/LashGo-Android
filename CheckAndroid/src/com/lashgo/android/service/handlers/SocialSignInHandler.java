package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
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

    public static final String SOCIAL_DTO = "social_dto";

    public SocialSignInHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws RetrofitError, IOException {
        SocialInfo socialInfo = (SocialInfo) intent.getSerializableExtra(SOCIAL_DTO);
        service.socialSignIn(socialInfo);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SOCIAL_DTO, socialInfo);
        return bundle;
    }
}
