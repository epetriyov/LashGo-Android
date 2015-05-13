package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.EventDto;
import com.lashgo.model.dto.ResponseList;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 20.10.2014.
 */
public class GetEventsHandler extends BaseIntentHandler {
    public GetEventsHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<EventDto> responseList = service.getEvents(intent.getBooleanExtra(ServiceExtraNames.SUBSCRIPTION_EVENTS.name(), false));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.EVENTS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
