package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.CommentDto;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 11.09.2014.
 */
public class AddCheckCommentHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        ResponseObject<CommentDto> responseObject = service.addCheckComment(intent.getIntExtra(ServiceExtraNames.CHECK_ID.name(), -1), intent.getStringExtra(ServiceExtraNames.COMMENT_TEXT.name()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceExtraNames.COMMENT.name(),responseObject.getResult());
        return bundle;
    }
}
