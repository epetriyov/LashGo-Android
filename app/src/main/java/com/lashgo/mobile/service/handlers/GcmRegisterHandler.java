package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.GcmRegistrationDto;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 20.03.14.
 */
public class GcmRegisterHandler extends BaseIntentHandler {

    public GcmRegisterHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.registerDevice((GcmRegistrationDto) intent.getSerializableExtra(ServiceExtraNames.GCM_REGISTRATION.name()));
        return intent.getExtras();
    }
}
