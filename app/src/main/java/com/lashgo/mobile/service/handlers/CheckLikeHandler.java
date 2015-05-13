package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 02.09.2014.
 */
public class CheckLikeHandler extends BaseIntentHandler {
    public CheckLikeHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<Boolean> isLikeAdded = service.likeCheck(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putBoolean(ServiceExtraNames.IS_LIKE_ADDED.name(),isLikeAdded.getResult());
        return bundle;
    }
}
