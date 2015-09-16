package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.SessionInfo;

import java.io.File;
import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by Eugene on 18.08.2014.
 */
public class SendPhotoHandler extends BaseIntentHandler {

    public SendPhotoHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        SessionInfo sessionInfo = settingsHelper.getSessionInfo();
        String photoPath = null;
        if (sessionInfo != null) {
            service.saveCheckPhoto(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1), new TypedFile("multipart/form-data", new File(intent.getStringExtra(ServiceExtraNames.PHOTO_PATH.name()))));
        }
        Bundle data = new Bundle();
        data.putString(ServiceExtraNames.PHOTO_PATH.name(), photoPath);
        return data;
    }
}
