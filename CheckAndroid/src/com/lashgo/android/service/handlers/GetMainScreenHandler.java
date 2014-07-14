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
    public static final String LAST_NEWS_VIEW_DATE = "last_news_view_date";
    public static final String LAST_SUBSCRIPTIONS_VIEW_DATE = "last_subscriptions_view_date";
    public static final String MAIN_SCREEN_INFO = "main_screen_info";

    @Override
    protected Bundle doExecute(Intent intent)
            throws IOException, RetrofitError {
        String lastNewsViewDate = intent.getStringExtra(LAST_NEWS_VIEW_DATE);
        String lastSubscriptionsViewDate = intent.getStringExtra(LAST_SUBSCRIPTIONS_VIEW_DATE);
        ResponseObject<MainScreenInfoDto> responseObject = service.getUserMainScreenInfo(lastNewsViewDate, lastSubscriptionsViewDate);
        Bundle result = new Bundle();
        result.putSerializable(MAIN_SCREEN_INFO, responseObject.getResult());
        return result;
    }
}
