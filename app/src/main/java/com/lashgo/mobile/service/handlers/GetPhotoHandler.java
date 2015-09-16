package com.lashgo.mobile.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.ResponseObject;

import java.io.IOException;

import retrofit.RetrofitError;

/**
 * Created by Eugene on 27.10.2014.
 */
public class GetPhotoHandler extends BaseIntentHandler {
    public GetPhotoHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<PhotoDto> responseObject = service.getPhoto(intent.getLongExtra(ServiceExtraNames.PHOTO_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.PHOTO_DTO.name(),responseObject.getResult());
        return bundle;
    }
}
