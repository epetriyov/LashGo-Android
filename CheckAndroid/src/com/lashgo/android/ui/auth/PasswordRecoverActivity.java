package com.lashgo.android.ui.auth;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.utils.ContextUtils;

/**
 * Created by Eugene on 12.09.2014.
 */
public class PasswordRecoverActivity extends BaseActivity {

    private EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP);
        setContentView(R.layout.act_password_recover);
        emailEdit = (EditText) findViewById(R.id.edit_email);
    }

    @Override
    public void startProgress() {
        showOverlayProgress();
    }

    @Override
    public void stopProgress() {
        hideOverlayProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recover_password, menu);
        ImageView applyBtn = (ImageView) menu.findItem(R.id.action_check).getActionView();
        applyBtn.setImageResource(R.drawable.ic_action_check);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(emailEdit.getText().toString())) {
                    serviceHelper.recoverPassword(emailEdit.getText().toString());
                } else {
                    emailEdit.setError(getString(R.string.error_empty_email));
                }
            }
        });
        return true;
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                ContextUtils.showToast(this, R.string.password_was_reset);
                finish();
            }
        }
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name());
    }

    @Override
    public void logout() {
        //TODO not to implement
    }

    @Override
    protected void refresh() {

    }
}
