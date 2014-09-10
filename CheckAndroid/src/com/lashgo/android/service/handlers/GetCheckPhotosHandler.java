package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.ResponseList;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 08.09.2014.
 */
public class GetCheckPhotosHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<PhotoDto> responseList = service.getCheckPhotos(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.PHOTOS_LIST.name(), responseList.getResultCollection());
        return bundle;
    }
}
