package com.lashgo.android;

import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.activity.ActivityFragment;
import com.lashgo.android.ui.check.CheckListFragment;
import com.lashgo.android.ui.check.CheckPhotosFragment;
import com.lashgo.android.ui.check.PhotoFragment;
import com.lashgo.android.ui.check.VoteFragment;
import com.lashgo.android.ui.news.NewsFragment;
import com.lashgo.android.ui.subscribes.SubscribesFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Eugene on 15.07.2014.
 */
@Module(
        injects = {
                CheckListFragment.class,
                NewsFragment.class,
                SubscribesFragment.class,
                ActivityFragment.class,
                PhotoFragment.class,
                VoteFragment.class,
                SubscribesFragment.class,
                CheckPhotosFragment.class,
                ServiceBinder.class
        },
        complete = false
)
public class FragmentModule {

    private final BaseFragment baseFragment;

    public FragmentModule(BaseFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    @Provides
    ServiceBinder provideServiceBinder() {
        return new ServiceBinder(baseFragment);
    }
}
