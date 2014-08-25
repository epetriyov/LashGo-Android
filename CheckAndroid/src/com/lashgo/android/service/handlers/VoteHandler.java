package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 25.08.2014.
 */
public class VoteHandler extends BaseIntentHandler {
    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        service.votePhoto((com.lashgo.model.dto.VoteAction) intent.getSerializableExtra(ServiceExtraNames.VOTE_ACTION.name()));
        return intent.getExtras();
    }
}
