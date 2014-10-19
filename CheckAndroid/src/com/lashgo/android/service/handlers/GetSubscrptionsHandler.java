package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.SubscriptionDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 20.10.2014.
 */
public class GetSubscrptionsHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<SubscriptionDto> responseList = service.getSubscriptions();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.SUBSCRIPTIONS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
