package com.lashgo.android.ui.subscribes;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;

/**
 * Created by Eugene on 20.10.2014.
 */
public class SubscribesActivity extends BaseActivity {

    private static final String SUBSCRIBES_TAG = "subscribes";
    private SubscribesFragment.ScreenType screenType;
    private int userId;

    public static Intent buildIntent(Context context, int userId, SubscribesFragment.ScreenType screenType) {
        Intent intent = new Intent(context, SubscribesActivity.class);
        intent.putExtra(SubscribesFragment.SCREEN_TYPE, screenType);
        intent.putExtra(ExtraNames.USER_ID.name(), userId);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SubscribesFragment.SCREEN_TYPE, screenType);
        outState.putInt(ExtraNames.USER_ID.name(), userId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            screenType = (SubscribesFragment.ScreenType) intent.getSerializableExtra(SubscribesFragment.SCREEN_TYPE);
            userId = intent.getIntExtra(ExtraNames.USER_ID.name(), -1);
        }
        if (screenType == null && savedInstanceState != null) {
            screenType = (SubscribesFragment.ScreenType) savedInstanceState.getSerializable(SubscribesFragment.SCREEN_TYPE);
            userId = savedInstanceState.getInt(ExtraNames.USER_ID.name());
        }
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        setContentView(R.layout.act_container);
        if (SubscribesFragment.ScreenType.SUBSCRIBERS.name().equals(screenType.name())) {
            setScreenTitle(R.string.subscribers_list);
        } else {
            setScreenTitle(R.string.subscribes_list);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, SubscribesFragment.newInstance(userId, screenType), SUBSCRIBES_TAG).commit();
    }

    @Override
    protected void refresh() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null) {
            ((BaseFragment) fragment).refresh();
        }
    }

    @Override
    public void logout() {
        if (settingsHelper.getUserId() == userId) {
            settingsHelper.logout();
            finish();
        }
    }
}
