package com.lashgo.android;

import com.facebook.UiLifecycleHelper;
import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.activity.ActivitiesActivity;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.auth.PasswordRecoverActivity;
import com.lashgo.android.ui.auth.SuccessfulRegisterActivity;
import com.lashgo.android.ui.auth.TwitterAuthActivity;
import com.lashgo.android.ui.check.*;
import com.lashgo.android.ui.comments.CommentsActivity;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.ui.photo.PhotoActivity;
import com.lashgo.android.ui.photo.PhotoFragment;
import com.lashgo.android.ui.profile.EditProfileActivity;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.ui.search.SearchActivity;
import com.lashgo.android.ui.start.SplashActivity;
import com.lashgo.android.ui.start.StartActivity;
import com.lashgo.android.ui.subscribes.SubscribesActivity;
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
                MainActivity.class,
                CheckBottomPanelController.class,
                CheckActivity.class,
                PhotoActivity.class,
                CheckPhotoActivity.class,
                PhotoFragment.class,
                VoteProcessActivity.class,
                ProfileActivity.class,
                LoginActivity.class,
                SuccessfulRegisterActivity.class,
                SubscribesActivity.class,
                ActivitiesActivity.class,
                SearchActivity.class,
                TwitterAuthActivity.class,
                PhotoSentActivity.class,
                CommentsActivity.class,
                EditProfileActivity.class,
                PasswordRecoverActivity.class,
                SplashActivity.class,
                FacebookHelper.class,
                TwitterHelper.class,
                StartActivity.class,
                VkontakteListener.class,
                ServiceBinder.class
        },
        complete = false
)
public class ActivityModule {

    private final BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    ServiceBinder provideServiceBinder() {
        return new ServiceBinder(baseActivity);
    }

    @Provides
    BaseActivity provideBaseActivity() {
        return baseActivity;
    }

    @Provides
    UiLifecycleHelper provideUiLifecycleHelper(FacebookHelper facebookHelper) {
        return new UiLifecycleHelper(baseActivity, facebookHelper.getFacebookCallback());
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
