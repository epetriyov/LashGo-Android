package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.CheckCounters;
import com.lashgo.model.dto.ResponseObject;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 14.09.2014.
 */
public class GetCheckCountersHandler extends BaseIntentHandler {
    public GetCheckCountersHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<CheckCounters> responseObject = service.getCheckCounters(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.COUNTERS.name(), responseObject.getResult());
        return bundle;
    }
}
