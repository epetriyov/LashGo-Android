package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.GcmRegistrationDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 20.03.14.
 */
public class GcmRegisterHandler extends BaseIntentHandler {
    public static final String GCM_REGISTRATION = "gcm_registration";

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.registerDevice((GcmRegistrationDto) intent.getSerializableExtra(GCM_REGISTRATION));
        return new Bundle();
    }
}
