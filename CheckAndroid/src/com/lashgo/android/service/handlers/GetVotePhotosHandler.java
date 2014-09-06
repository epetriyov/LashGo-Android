package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseObject;
import com.lashgo.model.dto.VotePhotosResult;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 25.08.2014.
 */
public class GetVotePhotosHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<VotePhotosResult> votePhotoResponseList = service.getVotePhotos(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1), intent.getBooleanExtra(ServiceExtraNames.IS_PHOTOS_COUNT_INCLUDED.name(), false));
        Bundle bundle = intent.getExtras();
        bundle.putSerializable(ServiceExtraNames.VOTE_PHOTO_LIST.name(), votePhotoResponseList.getResult());
        return bundle;
    }
}
