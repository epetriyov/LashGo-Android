package com.check.android.social;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class VkontakeListener extends VKSdkListener {

    private SocialErrorShower socialErrorShower;

    public VkontakeListener(SocialErrorShower socialErrorShower) {
        this.socialErrorShower = socialErrorShower;
    }

    @Override
    public void onReceiveNewToken(VKAccessToken newToken) {
        super.onReceiveNewToken(newToken);
        VKRequest request = VKApi.users().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                //TODO inner login
            }

            @Override
            public void onError(VKError error) {
                if (error.apiError != null) {
                    socialErrorShower.onDisplayError(error.apiError.errorMessage);
                } else {
                    socialErrorShower.onDisplayError(String.format("Error %d: %s", error.errorCode, error.errorMessage));
                }
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType,
                                   long bytesLoaded,
                                   long bytesTotal) {
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                socialErrorShower.onDisplayError(String.format("Attempt %d/%d failed\n", attemptNumber, totalAttempts));
            }
        });
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

    }

    @Override
    public void onAccessDenied(VKError authorizationError) {
        socialErrorShower.onDisplayError(authorizationError.errorMessage);
    }
}
