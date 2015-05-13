package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.SubscriptionDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 24.10.2014.
 */
public class GetCheckUsersHandler extends BaseIntentHandler {
    public GetCheckUsersHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<SubscriptionDto> responseList = service.getCheckUsers(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USERS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
