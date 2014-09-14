package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 15.09.2014.
 */
public class PhotoLikeHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<Boolean> isLikeAdded = service.likePhoto(intent.getLongExtra(ServiceExtraNames.PHOTO_ID.name(),-1));
        Bundle bundle = new Bundle();
        bundle.putBoolean(ServiceExtraNames.IS_LIKE_ADDED.name(),isLikeAdded.getResult());
        return bundle;
    }
}
