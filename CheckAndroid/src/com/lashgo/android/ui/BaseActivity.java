package com.lashgo.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import com.facebook.UiLifecycleHelper;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.SocialModule;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceCallbackListener;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.SocialInfo;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public abstract class BaseActivity extends Activity implements ServiceCallbackListener {

    private ObjectGraph loginGraph;

    @Inject
    protected ServiceHelper serviceHelper;

    @Inject
    protected SettingsHelper settingsHelper;

    @Inject
    protected UiLifecycleHelper facebookUiHelper;

    @Inject
    protected TwitterHelper twitterHelper;

    @Inject
    protected VkontakteListener vkSdkListener;

    @Inject
    protected FacebookHelper facebookHelper;

    @Inject
    protected Handler handler;

    private boolean isActivityOnForeground;

    private List<ServiceResult> serviceResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        loginGraph = LashgoApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        loginGraph.inject(this);
        super.onCreate(savedInstanceState);
        registerActionsListener();
        facebookUiHelper.onCreate(savedInstanceState);
        VKSdk.initialize(vkSdkListener, getString(R.string.vkontakte_app_id), null);
        twitterHelper.onCreate(savedInstanceState);
    }

    protected abstract void registerActionsListener();

    @Override
    protected void onDestroy() {
        facebookUiHelper.onDestroy();
        VKUIHelper.onDestroy(this);
        loginGraph = null;
        super.onDestroy();
        unregisterActionsListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        facebookUiHelper.onSaveInstanceState(outState);
        outState.putSerializable(TwitterHelper.KEY_REQUEST_TOKEN, twitterHelper.getRequestToken());
        super.onSaveInstanceState(outState);
    }

    protected abstract void unregisterActionsListener();

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOnForeground = true;
        deliverServiceResults();
        facebookUiHelper.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnForeground = false;
        facebookUiHelper.onPause();
    }

    private void deliverServiceResults() {
        for (ServiceResult serviceResult : serviceResultList) {
            onCommandFinished(serviceResult);
        }
    }

    @Override
    public void onCommandStarted() {
        setProgressBarIndeterminate(true);
    }

    private void onCommandFinished(ServiceResult serviceResult) {
        processServerResult(serviceResult.getAction(), serviceResult.getResultCode(), serviceResult.getData());
        serviceResultList.remove(serviceResult);
    }

    protected void processServerResult(String action, int resultCode, Bundle data) {

    }

    @Override
    public void onCommandFinished(String action, int resultCode, Bundle data) {
        setProgressBarIndeterminateVisibility(false);
        if (!isActivityOnForeground) {
            saveServiceResult(action, resultCode, data);
        } else {
            processServerResult(action, resultCode, data);
        }
    }

    private void saveServiceResult(String action, int resultCode, Bundle data) {
        serviceResultList.add(new ServiceResult(action, resultCode, data));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUiHelper.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void onDisplayError(final String errorMessage) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContextUtils.showToast(BaseActivity.this, errorMessage);
            }
        });
    }

    public void onSocialLogin(SocialInfo socialInfo) {
        serviceHelper.socialSignIn(socialInfo);
    }

    /**
     * Inject the supplied {@code object} using the activity-specific graph.
     */
    public void inject(Object object) {
        loginGraph.inject(object);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new SocialModule(this));
    }

    public void showErrorToast(Bundle data) {
        if (data != null) {
            ErrorDto errorDto = (ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA);
            if (errorDto != null && !TextUtils.isEmpty(errorDto.getErrorMessage())) {
                ContextUtils.showToast(this, errorDto.getErrorMessage());
            }
        }
    }


    public void showProgress() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgress() {
        setProgressBarIndeterminateVisibility(false);
    }
}
