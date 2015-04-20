package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 09.09.2014.
 */
public class GetCheckHandler extends BaseIntentHandler {
    public GetCheckHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<CheckDto> responseObject = service.getCheck(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.CHECK_DTO.name(),responseObject.getResult());
        return bundle;
    }
}
