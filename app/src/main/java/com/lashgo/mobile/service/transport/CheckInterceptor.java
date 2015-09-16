package com.lashgo.mobile.service.transport;

import android.text.TextUtils;

import com.lashgo.mobile.BuildConfig;
import com.lashgo.mobile.settings.SettingsHelper;
import com.lashgo.model.CheckApiHeaders;
import com.lashgo.model.dto.SessionInfo;

import java.util.UUID;

import retrofit.RequestInterceptor;

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
    }


    @Override
    public void intercept(RequestFacade requestFacade) {
        requestFacade.addHeader(CheckApiHeaders.uuid.name(), UUID.randomUUID().toString());
        requestFacade.addHeader(CheckApiHeaders.client_type.name(), BuildConfig.CLIENT_TYPE);
        SessionInfo session = settingsHelper.getSessionInfo();
        if (session != null && !TextUtils.isEmpty(session.getSessionId())) {
            requestFacade.addHeader(CheckApiHeaders.session_id.name(), session.getSessionId());
        }
    }

}
