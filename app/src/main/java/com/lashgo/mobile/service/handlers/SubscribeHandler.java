package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 20.10.2014.
 */
public class SubscribeHandler extends BaseIntentHandler {
    public SubscribeHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.subscribe((com.lashgo.model.dto.SubscribeDto) intent.getSerializableExtra(ServiceExtraNames.SUBSCRIPTION_DTO.name()));
        return intent.getExtras();
    }
}
