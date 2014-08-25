package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.ResponseList;
import com.lashgo.model.dto.VotePhoto;
import retrofit.RetrofitError;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Eugene on 25.08.2014.
 */
public class GetVotePhotosHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<VotePhoto> votePhotoResponseList = service.getVotePhotos();
        Bundle bundle = intent.getExtras();
        bundle.putSerializable(ServiceExtraNames.VOTE_PHOTO_LIST.name(), (ArrayList) votePhotoResponseList.getResultCollection());
        return bundle;
    }
}
