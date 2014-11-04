package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.SubscriptionDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 30.10.2014.
 */
public class GetPhotoUsersHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<SubscriptionDto> responseList = service.getVoteUsers(intent.getLongExtra(BaseActivity.ExtraNames.PHOTO_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.USERS_DTO.name(), responseList.getResultCollection());
        return bundle;
    }
}
