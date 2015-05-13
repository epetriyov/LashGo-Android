package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 31.10.2014.
 */
public class ComplainPhotoHandler extends BaseIntentHandler {
    public ComplainPhotoHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.comlainPhoto(intent.getLongExtra(ServiceExtraNames.PHOTO_ID.name(), -1));
        return intent.getExtras();
    }
}
