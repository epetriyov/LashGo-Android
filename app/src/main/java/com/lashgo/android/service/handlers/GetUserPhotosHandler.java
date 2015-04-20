package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.ResponseList;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 08.09.2014.
 */
public class GetUserPhotosHandler extends BaseIntentHandler {
    public GetUserPhotosHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<PhotoDto> responseList = service.getUserPhotos(intent.getIntExtra(ServiceExtraNames.USER_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.PHOTOS_LIST.name(),responseList.getResultCollection());
        return bundle;
    }
}
