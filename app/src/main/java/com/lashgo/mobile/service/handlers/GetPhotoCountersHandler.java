package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.CheckCounters;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 14.09.2014.
 */
public class GetPhotoCountersHandler extends BaseIntentHandler {
    public GetPhotoCountersHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<CheckCounters> responseObject = service.getPhotoCounters(intent.getLongExtra(ServiceExtraNames.PHOTO_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.COUNTERS.name(), responseObject.getResult());
        return bundle;
    }
}
