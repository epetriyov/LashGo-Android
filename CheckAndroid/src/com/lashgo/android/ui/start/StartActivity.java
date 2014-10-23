package com.lashgo.android.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.main.MainActivity;

/**
 * Created by Eugene on 24.06.2014.
 */
public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!settingsHelper.isLoggedIn()) {
            startActivity(new Intent(this, SplashActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }
}
