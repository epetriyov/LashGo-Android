package com.check.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.check.android.service.*;
import com.check.android.service.handlers.*;
import com.check.android.settings.SettingsHelper;
import com.check.android.ui.BaseActivity;
import com.check.android.ui.auth.LoginActivity;
import com.check.android.ui.auth.RegisterActivity;
import com.check.android.ui.start.StartActivity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 13.03.14
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
@Module(
        injects = {
                RegisterActivity.class,
                StartActivity.class,
                LoginHandler.class,
                RegisterHandler.class,
                GcmRegisterHandler.class,
                SocialSignInHandler.class
        },
        library = true
)
public class CheckModule {

    private final CheckApplication application;

    public CheckModule(CheckApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    SettingsHelper provideSettingsHelper() {
        return new SettingsHelper(application);
    }

    @Provides
    @Singleton
    ServiceHelper provideServiceHelper() {
        return new ServiceHelper(application);
    }

    @Provides
    @Singleton
    RestService provideRestService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(CheckConfig.BASE_URL)
                .setConverter(new JacksonConverter(objectMapper))
                .setErrorHandler(new CheckErrorHandler())
                .setRequestInterceptor(new CheckInterceptor())
                .build();
        return restAdapter.create(RestService.class);
    }


    @Provides
    Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
