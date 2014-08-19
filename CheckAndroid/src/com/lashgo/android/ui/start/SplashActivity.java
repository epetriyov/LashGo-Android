package com.lashgo.android.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.lashgo.android.R;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.main.MainActivity;

/**
 * Created by Eugene on 02.03.14.
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    public static final int SPASH_REQUEST_CODE = 1;

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
            finish();
        } else if (v.getId() == R.id.btn_enter) {
            startActivityForResult(new Intent(this, LoginActivity.class), SPASH_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPASH_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }
}
