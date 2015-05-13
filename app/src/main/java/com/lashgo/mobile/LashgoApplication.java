package com.lashgo.mobile;


import android.app.Application;
import android.os.StrictMode;
import com.facebook.FacebookSdk;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lashgo.mobile.service.ServiceHelper;
import com.lashgo.mobile.service.transport.CheckErrorHandler;
import com.lashgo.mobile.service.transport.CheckInterceptor;
import com.lashgo.mobile.service.transport.JacksonConverter;
import com.lashgo.mobile.service.transport.RestService;
import com.lashgo.mobile.settings.SettingsHelper;
import retrofit.RestAdapter;

import java.text.SimpleDateFormat;

/**
 * Created by Eugene on 18.02.14.
 */
public class LashgoApplication extends Application {

    private static LashgoApplication instance;

    public static LashgoApplication getInstance() {
        return instance;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isWasSent() {
        return wasSent;
    }

    public void setWasSent(boolean wasSent) {
        this.wasSent = wasSent;
    }

    //TODO hack
    private String imgPath;

    private boolean wasSent;

    private volatile RestService restService;

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    private ServiceHelper serviceHelper;

    public SettingsHelper getSettingsHelper() {
        return settingsHelper;
    }

    private SettingsHelper settingsHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        settingsHelper = SettingsHelper.getInstance(this);
        serviceHelper = ServiceHelper.getInstance(this);
    }

    public void clearHackData() {
        imgPath = null;
        wasSent = false;
    }

    public RestService getRestService() {
        if (restService == null) {
            synchronized (RestService.class) {
                if (restService == null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    objectMapper.setDateFormat(new SimpleDateFormat(LashgoConfig.DATE_FORMAT));
                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(LashgoConfig.BASE_URL)
                            .setConverter(new JacksonConverter(objectMapper))
                            .setErrorHandler(new CheckErrorHandler())
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setRequestInterceptor(new CheckInterceptor(settingsHelper))
                            .build();
                    restService = restAdapter.create(RestService.class);
                }
            }
        }
        return restService;
    }
}
