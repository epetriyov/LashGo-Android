package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.SubscriptionDto;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 30.10.2014.
 */
public class GetPhotoUsersHandler extends BaseIntentHandler {
    public GetPhotoUsersHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<SubscriptionDto> responseList = service.getVoteUsers(intent.getLongExtra(BaseActivity.ExtraNames.PHOTO_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USERS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
