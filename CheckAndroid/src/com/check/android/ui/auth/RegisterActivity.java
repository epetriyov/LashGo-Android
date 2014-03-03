package com.check.android.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.check.android.R;
import com.check.android.service.handlers.BaseIntentHandler;
import com.check.android.service.handlers.RestHandlerFactory;
import com.check.android.service.handlers.SocialSignInHandler;
import com.check.android.ui.BaseActivity;
import com.check.android.utils.Md5Util;
import com.check.model.dto.RegisterInfo;
import com.check.model.dto.SocialInfo;
import org.holoeverywhere.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 02.03.14
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String SOCIAL_INFO = "social_info";
    private EditText login;
    private EditText password;
    private EditText email;
    private SocialInfo socialInfo;

    public static Intent buildIntent(Context context, SocialInfo socialInfo) {
        Intent intent = buildIntent(context);
        intent.putExtra(SOCIAL_INFO, socialInfo);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SOCIAL_INFO, socialInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            socialInfo = (SocialInfo) intent.getSerializableExtra(SOCIAL_INFO);
        } else if (savedInstanceState != null) {
            socialInfo = (SocialInfo) savedInstanceState.getSerializable(SOCIAL_INFO);
        }
        setContentView(R.layout.act_register);
        login = (EditText) findViewById(R.id.edit_login);
        password = (EditText) findViewById(R.id.edit_password);
        email = (EditText) findViewById(R.id.edit_email);
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    protected void registerActionsListener() {
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_REGISTER, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN, this);
    }

    @Override
    protected void unregisterActionsListener() {
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
    }

    @Override
    protected void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
            Toast.makeText(this, data.getString(BaseIntentHandler.ERROR_EXTRA), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, data.getString(SocialSignInHandler.SESSION_INFO), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            String loginValue = login.getText().toString();
            String passwordValue = password.getText().toString();
            String emailValue = email.getText().toString();
            if (TextUtils.isEmpty(loginValue)) {
                login.setError(getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(passwordValue)) {
                password.setError(getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(emailValue)) {
                email.setError(getString(R.string.error_empty_field));
            } else {
                if (socialInfo == null) {
                    serviceHelper.register(new RegisterInfo(login.getText().toString(), password.getText().toString(), email.getText().toString()));
                } else {
                    socialInfo.setLogin(loginValue);
                    socialInfo.setEmail(emailValue);
                    socialInfo.setPasswordHash(Md5Util.md5(passwordValue));
                    serviceHelper.socialSignIn(socialInfo);
                }
            }
        }
    }

    public static Intent buildIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }
}
