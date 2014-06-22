package com.lashgo.android;

import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.SocialErrorShower;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.ui.auth.LoginActivity;
import com.facebook.UiLifecycleHelper;
import dagger.Module;
import dagger.Provides;

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
    @ForActivity
    LoginActivity provideContext() {
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
