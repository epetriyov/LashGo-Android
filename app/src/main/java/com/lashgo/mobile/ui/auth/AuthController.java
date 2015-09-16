package com.lashgo.mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.lashgo.mobile.R;
import com.lashgo.mobile.service.ServiceHelper;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.social.TwitterHelper;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.utils.Md5Util;
import com.lashgo.mobile.utils.UiUtils;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.RegisterResponse;
import com.lashgo.model.dto.SocialInfo;
import com.lashgo.model.dto.SocialNames;
import com.lashgo.model.dto.UserDto;
import com.vk.sdk.VKSdk;

import java.util.Arrays;

/**
 * Created by Eugene on 09.07.2014.
 */
public class AuthController implements View.OnClickListener {

    private static final String[] FACEBOOK_PERMISSION = {"public_profile ", "email", "user_about_me", "user_birthday"};

    private static final String[] VK_PERMISSIONS = {"email"};

    private EditText login;

    private EditText password;

    private BaseActivity baseActivity;

    private ServiceHelper serviceHelper;

    private TwitterHelper twitterHelper;

    private LoginActivity.OpenMode openMode;

    private AuthListener authListener;

    private CallbackManager callbackManager;

    public AuthController(BaseActivity baseActivity, ServiceHelper serviceHelper, CallbackManager callbackManager, TwitterHelper twitterHelper, AuthListener authListener) {
        this.baseActivity = baseActivity;
        this.serviceHelper = serviceHelper;
        this.callbackManager = callbackManager;
        this.twitterHelper = twitterHelper;
        this.authListener = authListener;
    }

    public void initViews(View rootView) {
        login = (EditText) rootView.findViewById(R.id.edit_email);
        password = (EditText) rootView.findViewById(R.id.edit_password);
        rootView.findViewById(R.id.btn_login).setOnClickListener(this);
        rootView.findViewById(R.id.btn_register).setOnClickListener(this);
        rootView.findViewById(R.id.btn_vk).setOnClickListener(this);
        rootView.findViewById(R.id.btn_twitter).setOnClickListener(this);
        rootView.findViewById(R.id.btn_fb).setOnClickListener(this);
        rootView.findViewById(R.id.btn_recover_password).setOnClickListener(this);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                serviceHelper.socialSignIn(new SocialInfo(loginResult.getAccessToken().getToken(), null, SocialNames.FACEBOOK));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_vk) {
            VKSdk.authorize(VK_PERMISSIONS);
        } else if (view.getId() == R.id.btn_fb) {
            LoginManager.getInstance().logInWithReadPermissions(baseActivity, Arrays.asList(FACEBOOK_PERMISSION));
        } else if (view.getId() == R.id.btn_twitter) {
            twitterHelper.loginWithTwitter();
        } else if (view.getId() == R.id.btn_login) {
            UiUtils.hideSoftKeyboard(login);
            LoginInfo loginInfo = buildLoginInfo();
            if (loginInfo != null) {
                serviceHelper.login(loginInfo);
            }
        } else if (view.getId() == R.id.btn_recover_password) {
            baseActivity.startActivity(new Intent(baseActivity, PasswordRecoverActivity.class));
        } else if (view.getId() == R.id.btn_register) {
            UiUtils.hideSoftKeyboard(login);
            LoginInfo loginInfo = buildLoginInfo();
            if (loginInfo != null) {
                serviceHelper.register(loginInfo);
            }
        }
    }

    private LoginInfo buildLoginInfo() {
        String loginValue = login.getText().toString();
        String passwordValue = password.getText().toString();
        if (TextUtils.isEmpty(loginValue)) {
            login.setError(baseActivity.getString(R.string.error_empty_email));
        } else if (TextUtils.isEmpty(passwordValue)) {
            password.setError(baseActivity.getString(R.string.error_empty_password));
        } else {
            return new LoginInfo(login.getText().toString(), Md5Util.md5(password.getText().toString()));
        }
        return null;
    }

    public void handleServerResponse(String action, int resultCode, Bundle data) {
        if (BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                onLoginSuccessFull();
            } else {
                baseActivity.showErrorToast(data);
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                if (data != null) {
                    onRegisterSuccessFull(((RegisterResponse) data.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name())).getUserDto());
                }
            } else {
                baseActivity.showErrorToast(data);
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name().equals(action)) {
            if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
                baseActivity.showErrorToast(data);
            } else {
                if (data != null) {
                    UserDto userDto = ((RegisterResponse) data.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name())).getUserDto();
                    if (userDto != null) {
                        onRegisterSuccessFull(userDto);
                    } else {
                        onLoginSuccessFull();
                    }
                }
            }
        }

    }

    private void onRegisterSuccessFull(UserDto registerResponse) {
        if (registerResponse != null) {
            if (registerResponse.getLogin() != null) {
                if (authListener != null) {
                    authListener.onRegisterSuccessFull(registerResponse);
                }
            } else {
                onLoginSuccessFull();
            }
        }
    }

    private void onLoginSuccessFull() {
        if (authListener != null) {
            authListener.onLoginSuccessFull();
        }
    }

    public void setOpenMode(LoginActivity.OpenMode openMode) {
        this.openMode = openMode;
    }

    public LoginActivity.OpenMode getOpenMode() {
        return openMode;
    }

    public interface AuthListener {
        void onLoginSuccessFull();

        void onRegisterSuccessFull(UserDto userDto);
    }
}
