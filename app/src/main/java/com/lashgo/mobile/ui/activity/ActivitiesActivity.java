package com.lashgo.mobile.ui.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.lashgo.mobile.R;
import com.lashgo.mobile.ui.BaseActivity;

import java.util.Date;

/**
 * Created by Eugene on 20.10.2014.
 */
public class ActivitiesActivity extends BaseActivity {

    private static final String ACTIVITIES_TAG = "activities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        setContentView(R.layout.act_container);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new ActivityFragment(), ACTIVITIES_TAG).commit();
        settingsHelper.setLastSubscriptionsView(new Date());
    }

    @Override
    protected void refresh() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment != null)
        {
            ((ActivityFragment)fragment).refresh();
        }
    }

    @Override
    public void logout() {
        finish();
    }

}
