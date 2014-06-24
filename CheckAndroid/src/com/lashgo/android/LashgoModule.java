package com.lashgo.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lashgo.android.service.*;
import com.lashgo.android.service.handlers.GcmRegisterHandler;
import com.lashgo.android.service.handlers.LoginHandler;
import com.lashgo.android.service.handlers.RegisterHandler;
import com.lashgo.android.service.handlers.SocialSignInHandler;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.ui.start.SplashActivity;
import com.lashgo.android.ui.start.StartActivity;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

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
                StartActivity.class,
                MainActivity.class,
                LoginActivity.class,
                LoginHandler.class,
                RegisterHandler.class,
                GcmRegisterHandler.class,
                SocialSignInHandler.class
        },
        library = true
)
public class LashgoModule {

    private final LashgoApplication application;

    public LashgoModule(LashgoApplication application) {
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
                .setEndpoint(LashgoConfig.BASE_URL)
                .setConverter(new JacksonConverter(objectMapper))
                .setErrorHandler(new CheckErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new ApacheClient())
                .setRequestInterceptor(new CheckInterceptor())
                .build();
        return restAdapter.create(RestService.class);
    }


    @Provides
    Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
