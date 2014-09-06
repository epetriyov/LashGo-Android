package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.SessionInfo;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Eugene on 18.08.2014.
 */
public class SendPhotoHandler extends BaseIntentHandler {

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        SessionInfo sessionInfo = settingsHelper.getSessionInfo();
        if (sessionInfo != null) {
            service.saveCheckPhoto(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1), new TypedFile("multipart/form-data", new File(intent.getStringExtra(ServiceExtraNames.PHOTO_PATH.name()))));
        }
        return intent.getExtras();
    }
}
