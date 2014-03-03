package com.check.android.social;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.check.android.R;
import com.check.android.ui.auth.RegisterActivity;
import com.check.model.SocialTypes;
import com.check.model.dto.SocialInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.*;
import junit.framework.Assert;
import org.holoeverywhere.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 23.02.14
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class VkontakteListener extends VKSdkListener {

    private final Handler handler;
    private SocialErrorShower socialErrorShower;

    private Context context;

    public VkontakteListener(Context context, SocialErrorShower socialErrorShower) {
        this.socialErrorShower = socialErrorShower;
        this.context = context;
        handler = new Handler();
    }

    @Override
    public void onReceiveNewToken(VKAccessToken newToken) {
        super.onReceiveNewToken(newToken);
        VKRequest request = VKApi.users().get(VKParameters.from("bdate", "photo_100", VKApiConst.SEX));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (response != null && response.json != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        VkUser vkUser = objectMapper.readValue(response.json.toString(), VkUser.class);
                        if (vkUser != null) {
                            final SocialInfo socialInfo = new SocialInfo();
                            socialInfo.setSurname(vkUser.getLastName());
                            socialInfo.setSocialType(SocialTypes.VK);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            if (!TextUtils.isEmpty(vkUser.getBirthDate())) {
                                try {
                                    Date birthDate = dateFormat.parse(vkUser.getBirthDate());
                                    socialInfo.setBirthDay(birthDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            socialInfo.setAvatarUrl(vkUser.getAvatarUrl());
                            socialInfo.setName(vkUser.getFirstName());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(RegisterActivity.buildIntent(context, socialInfo));
                                }
                            });
                        } else {
                            Assert.fail();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.wrong_response, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
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
