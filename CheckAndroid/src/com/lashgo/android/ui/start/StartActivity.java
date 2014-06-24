package com.lashgo.android.ui.start;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.main.MainActivity;
import org.holoeverywhere.app.Activity;

import javax.inject.Inject;

/**
 * Created by Eugene on 24.06.2014.
 */
public class StartActivity extends Activity {

    @Inject
    private SettingsHelper settingsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LashgoApplication.getInstance().getApplicationGraph().inject(this);
        if (settingsHelper.isFirstLaunch()) {
            startActivity(new Intent(this, SplashActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
