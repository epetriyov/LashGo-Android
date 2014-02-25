package com.check.android.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.check.android.CheckApplication;
import com.check.android.R;
import com.check.android.social.FacebookHelper;
import com.check.android.social.SocialErrorShower;
import com.check.android.social.TwitterHelper;
import com.check.android.social.VkontakeListener;
import com.check.android.ui.BaseActivity;
import com.check.model.dto.LoginInfo;
import com.check.model.dto.SessionInfo;
import com.facebook.UiLifecycleHelper;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

import java.util.UUID;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, SocialErrorShower {

    private Handler handler = new Handler();

    private UiLifecycleHelper facebookUiHelper;

    private TwitterHelper twitterHelper;

    private VkontakeListener vkSdkListener;

    private FacebookHelper facebookHelper;

    private EditText login;

    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        login = (EditText) findViewById(R.id.edit_login);
        password = (EditText) findViewById(R.id.edit_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_auth_facebook).setOnClickListener(this);
        findViewById(R.id.btn_auth_vk).setOnClickListener(this);
        findViewById(R.id.btn_auth_twitter).setOnClickListener(this);
        facebookHelper = new FacebookHelper();
        facebookUiHelper = new UiLifecycleHelper(this, facebookHelper.getFacebookCallback());
        facebookUiHelper.onCreate(savedInstanceState);
        vkSdkListener = new VkontakeListener(this);
        VKSdk.initialize(vkSdkListener, getString(R.string.vkontakte_app_id), null);
        twitterHelper = new TwitterHelper(this, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        facebookUiHelper.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            twitterHelper.handleCallbackUrl(this, intent.getData());
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
        facebookUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookUiHelper.onDestroy();
        VKUIHelper.onDestroy(this);
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
            twitterHelper.loginWithTwitter(this);
        } else if (view.getId() == R.id.btn_login) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SessionInfo sessionInfo = CheckApplication.getInstance().getService().login(UUID.randomUUID().toString(), new LoginInfo(login.getText().toString(), password.getText().toString()));
                    Log.d("SessionInfo", sessionInfo.getSessionId());
                }
            }).start();
        } else if (view.getId() == R.id.btn_register) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SessionInfo sessionInfo = CheckApplication.getInstance().getService().register(UUID.randomUUID().toString(), new LoginInfo(login.getText().toString(), password.getText().toString()));
                    Log.d("SessionInfo", sessionInfo.getSessionId());
                }
            }).start();
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
