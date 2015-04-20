package com.lashgo.android.service.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.CommentDto;
import com.lashgo.model.dto.ResponseList;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 11.09.2014.
 */
public class GetPhotoCommentsHandler extends BaseIntentHandler {
    public GetPhotoCommentsHandler(Context context) {
        super(context);
    }

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseList<CommentDto> responseList = service.getPhotoComments(intent.getLongExtra(ServiceExtraNames.PHOTO_ID.name(), -1));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.COMMENTS_LIST.name(), responseList.getResultCollection());
        return bundle;
    }
}
