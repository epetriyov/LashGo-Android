package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.CheckDto;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 16.06.2014.
 */
public class GetLastCheckHandler extends BaseIntentHandler {

    public static final String CHECK_DTO = "check_dto";

    @Override
    protected Bundle doExecute(Intent intent) throws IOException, RetrofitError {
        Bundle bundle = new Bundle();
        CheckDto checkDto = service.getLastCheck();
        bundle.putSerializable(CHECK_DTO, checkDto);
        return bundle;
    }
}
