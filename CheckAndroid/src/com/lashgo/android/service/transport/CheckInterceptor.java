package com.lashgo.android.service.transport;

import android.text.TextUtils;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.model.CheckApiHeaders;
import retrofit.RequestInterceptor;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 26.02.14
 * Time: 0:08
 * To change this template use File | Settings | File Templates.
 */
public class CheckInterceptor implements RequestInterceptor {

    private SettingsHelper settingsHelper;

    public CheckInterceptor(SettingsHelper settingsHelper) {
        this.settingsHelper = settingsHelper;
        LashgoApplication.getInstance().inject(this);
    }


    @Override
    public void intercept(RequestFacade requestFacade) {
        requestFacade.addHeader(CheckApiHeaders.UUID, UUID.randomUUID().toString());
        requestFacade.addHeader(CheckApiHeaders.CLIENT_TYPE, LashgoConfig.CLIENT_TYPE);
        String session = settingsHelper.getSessionId();
        if (!TextUtils.isEmpty(session)) {
            requestFacade.addHeader(CheckApiHeaders.SESSION_ID, session);
        }
    }

}
