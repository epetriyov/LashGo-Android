package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ExtendedSocialInfo;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 24.06.2014.
 */
public class SocialSignUpHandler extends BaseIntentHandler {

    public static final String EXTENDED_SOCIAL_DTO = "extended_social_dto";

    public SocialSignUpHandler() {
        super();
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ExtendedSocialInfo extendedSocialInfo = (ExtendedSocialInfo) intent.getSerializableExtra(EXTENDED_SOCIAL_DTO);
        service.confirmSocialSignUp(extendedSocialInfo);
        Bundle bundle = new Bundle();
        return bundle;
    }
}
