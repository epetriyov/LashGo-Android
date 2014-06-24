package com.lashgo.android.social;

import com.lashgo.android.ForActivity;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.model.dto.SocialInfo;
import com.lashgo.model.dto.SocialNames;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class VkontakteListener extends VKSdkListener {

    @Inject
    @ForActivity
    private BaseActivity loginActivity;

    public VkontakteListener(BaseActivity baseActivity) {
        baseActivity.inject(this);
    }

    @Override
    public void onReceiveNewToken(VKAccessToken newToken) {
        super.onReceiveNewToken(newToken);
        loginActivity.onSocialLogin(new SocialInfo(newToken.accessToken, newToken.secret, SocialNames.VK));
    }

    @Override
    public void onAcceptUserToken(VKAccessToken token) {
        super.onAcceptUserToken(token);
    }

    @Override
    public void onRenewAccessToken(VKAccessToken token) {
        super.onRenewAccessToken(token);
    }

    @Override
    public void onCaptchaError(VKError captchaError) {
        new VKCaptchaDialog(captchaError).show();
    }

    @Override
    public void onTokenExpired(VKAccessToken expiredToken) {
        VKSdk.authorize("email");
    }

    @Override
    public void onAccessDenied(VKError authorizationError) {
        loginActivity.onDisplayError(authorizationError.errorMessage);
    }
}
