package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.SubscriptionDto;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 23.10.2014.
 */
public class FindUserHandler extends BaseIntentHandler {
    public FindUserHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<SubscriptionDto> responseList = service.findUsers(intent.getStringExtra(ServiceExtraNames.SEARCH_TEXT.name()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USERS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
