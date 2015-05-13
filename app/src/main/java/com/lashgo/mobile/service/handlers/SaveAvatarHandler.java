package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Eugene on 10.09.2014.
 */
public class SaveAvatarHandler extends BaseIntentHandler {
    public SaveAvatarHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.saveAvatar(new TypedFile("multipart/form-data", new File(intent.getStringExtra(ServiceExtraNames.AVATAR_PATH.name()))));
        return intent.getExtras();
    }
}
