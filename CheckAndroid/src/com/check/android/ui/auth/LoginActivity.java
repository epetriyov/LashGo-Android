package com.check.android.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import com.check.android.CheckApplication;
import com.check.android.LoginModule;
import com.check.android.R;
import com.check.android.service.handlers.BaseIntentHandler;
import com.check.android.service.handlers.RestHandlerFactory;
import com.check.android.social.FacebookHelper;
import com.check.android.social.SocialErrorShower;
import com.check.android.social.TwitterHelper;
import com.check.android.social.VkontakteListener;
import com.check.android.ui.BaseActivity;
import com.check.android.utils.Md5Util;
import com.check.model.dto.LoginInfo;
import com.facebook.UiLifecycleHelper;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import dagger.ObjectGraph;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, SocialErrorShower {

    private static final String RECOVER_URL = "";

    private ObjectGraph loginGraph;

    @Inject
    Handler handler;

    @Inject
    UiLifecycleHelper facebookUiHelper;

    @Inject
    TwitterHelper twitterHelper;

    @Inject
    VkontakteListener vkSdkListener;

    @Inject
    FacebookHelper facebookHelper;

    private EditText login;

    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginGraph = CheckApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        loginGraph.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        login = (EditText) findViewById(R.id.edit_login);
        password = (EditText) findViewById(R.id.edit_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_auth_facebook).setOnClickListener(this);
        findViewById(R.id.btn_auth_vk).setOnClickListener(this);
        findViewById(R.id.btn_auth_twitter).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_password_recover).setOnClickListener(this);
        facebookUiHelper.onCreate(savedInstanceState);
        VKSdk.initialize(vkSdkListener, getString(R.string.vkontakte_app_id), null);
        twitterHelper.onCreate(savedInstanceState);
    }

    /**
     * Inject the supplied {@code object} using the activity-specific graph.
     */
    public void inject(Object object) {
        loginGraph.inject(object);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new LoginModule(this));
    }

    @Override
    protected void registerActionsListener() {
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_LOGIN, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN, this);
    }


    @Override
    public void onResume() {
        super.onResume();
//        facebookUiHelper.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            twitterHelper.handleCallbackUrl(intent.getData());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUiHelper.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
//        facebookUiHelper.onPause();
    }

    @Override
    protected void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
            Toast.makeText(this, data.getString(BaseIntentHandler.ERROR_EXTRA), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.auth_success, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
//        facebookUiHelper.onDestroy();
        VKUIHelper.onDestroy(this);
        loginGraph = null;
        super.onDestroy();
    }

    @Override
    protected void unregisterActionsListener() {
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_LOGIN);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        facebookUiHelper.onSaveInstanceState(outState);
        outState.putSerializable(TwitterHelper.KEY_REQUEST_TOKEN, twitterHelper.getRequestToken());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_auth_facebook) {
            facebookHelper.loginWithFacebook(this);
        } else if (view.getId() == R.id.btn_auth_vk) {
            VKSdk.authorize();
        } else if (view.getId() == R.id.btn_auth_twitter) {
            twitterHelper.loginWithTwitter();
        } else if (view.getId() == R.id.btn_login) {
            String loginValue = login.getText().toString();
            String passwordValue = password.getText().toString();
            if (TextUtils.isEmpty(loginValue)) {
                login.setError(getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(passwordValue)) {
                password.setError(getString(R.string.error_empty_field));
            } else {
                serviceHelper.login(new LoginInfo(login.getText().toString(), Md5Util.md5(password.getText().toString())));
            }
        } else if (view.getId() == R.id.btn_register) {
            startActivity(RegisterActivity.buildIntent(this));
        } else if (view.getId() == R.id.btn_password_recover) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RECOVER_URL)));
        }
    }

    @Override
    public void onDisplayError(final String errorMessage) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
