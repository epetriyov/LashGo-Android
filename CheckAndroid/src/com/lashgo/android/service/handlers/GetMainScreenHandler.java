package com.lashgo.android.service.handlers;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.model.dto.MainScreenInfoDto;
import com.lashgo.model.dto.ResponseObject;
import retrofit.RetrofitError;

import java.io.IOException;

/**
 * Created by Eugene on 09.07.2014.
 */
public class GetMainScreenHandler extends BaseIntentHandler {

    @Override
    protected Bundle doExecute(Intent intent)
            throws IOException, RetrofitError {
        String lastNewsViewDate = intent.getStringExtra(ServiceExtraNames.LAST_NEWS_VIEW_DATE.name());
        String lastSubscriptionsViewDate = intent.getStringExtra(ServiceExtraNames.LAST_SUBSCRIPTIONS_VIEW_DATE.name());
        ResponseObject<MainScreenInfoDto> responseObject = service.getUserMainScreenInfo(lastNewsViewDate, lastSubscriptionsViewDate);
        Bundle result = new Bundle();
        result.putSerializable(ServiceExtraNames.MAIN_SCREEN_INFO.name(), responseObject.getResult());
        return result;
    }
}
