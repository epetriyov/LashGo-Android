package com.check.android;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.check.android.social.FacebookHelper;
import com.check.android.social.SocialErrorShower;
import com.check.android.social.TwitterHelper;
import com.check.android.social.VkontakteListener;
import com.check.android.ui.auth.LoginActivity;
import com.facebook.UiLifecycleHelper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 14.03.14
 * Time: 0:59
 * To change this template use File | Settings | File Templates.
 */
@Module(
        injects = {
                LoginActivity.class,
                FacebookHelper.class,
                TwitterHelper.class,
                VkontakteListener.class
        },
        complete = false
)
public class LoginModule {

    private final LoginActivity loginActivity;

    public LoginModule(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Provides
    UiLifecycleHelper provideUiLifecycleHelper(FacebookHelper facebookHelper) {
        return new UiLifecycleHelper(loginActivity, facebookHelper.getFacebookCallback());
    }

    @Provides
    Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @ForActivity
    Context provideContext() {
        return loginActivity;
    }

    @Provides
    FacebookHelper provideFacebookHelper() {
        return new FacebookHelper(loginActivity);
    }

    @Provides
    VkontakteListener provideVkontakteListener() {
        return new VkontakteListener(loginActivity);
    }

    @Provides
    TwitterHelper provideTwitterHelper() {
        return new TwitterHelper(loginActivity);
    }

    @Provides
    SocialErrorShower provideSocialErrorShower() {
        return loginActivity;
    }
}
