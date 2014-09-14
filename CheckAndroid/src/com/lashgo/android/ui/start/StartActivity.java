package com.lashgo.android.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.main.MainActivity;

/**
 * Created by Eugene on 24.06.2014.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SettingsHelper(this).isLoggedIn()) {
            startActivity(new Intent(this, SplashActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
