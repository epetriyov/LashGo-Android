package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 12.09.2014.
 */
public class PasswordRecoverHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.passwordRecover(intent.getStringExtra(ServiceExtraNames.EMAIL.name()));
        return null;
    }
}
