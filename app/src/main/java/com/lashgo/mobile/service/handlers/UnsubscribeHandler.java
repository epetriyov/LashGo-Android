package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 20.10.2014.
 */
public class UnsubscribeHandler extends BaseIntentHandler {
    public UnsubscribeHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.unsubscribe(intent.getIntExtra(ServiceExtraNames.USER_ID.name(), -1));
        return intent.getExtras();
    }
}
