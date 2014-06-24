package com.lashgo.android.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.lashgo.android.R;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.main.MainActivity;
import org.holoeverywhere.app.Activity;

/**
 * Created by Eugene on 02.03.14.
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (v.getId() == R.id.btn_enter) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
