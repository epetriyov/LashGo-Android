package com.lashgo.android;

import com.facebook.UiLifecycleHelper;
import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.ui.BaseActivity;
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
                BaseActivity.class,
                FacebookHelper.class,
                TwitterHelper.class,
                VkontakteListener.class
        },
        complete = false
)
public class SocialModule {

    private final BaseActivity baseActivity;

    public SocialModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    UiLifecycleHelper provideUiLifecycleHelper(FacebookHelper facebookHelper) {
        return new UiLifecycleHelper(baseActivity, facebookHelper.getFacebookCallback());
    }

    @Provides
    @ForActivity
    BaseActivity provideContext() {
        return baseActivity;
    }

    @Provides
    FacebookHelper provideFacebookHelper() {
        return new FacebookHelper(baseActivity);
    }

    @Provides
    VkontakteListener provideVkontakteListener() {
        return new VkontakteListener(baseActivity);
    }

    @Provides
    TwitterHelper provideTwitterHelper() {
        return new TwitterHelper(baseActivity);
    }
}
