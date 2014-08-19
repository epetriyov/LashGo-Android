package com.lashgo.android.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import com.facebook.UiLifecycleHelper;
import com.lashgo.android.ActivityModule;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.ServiceReceiver;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public abstract class BaseActivity extends Activity implements ServiceReceiver {

    protected static final String PROGRESS_DIALOG = "progress";

    public static enum ExtraNames {CHECK_DTO, PHOTO_URL}

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

    @Inject
    ServiceBinder serviceBinder;

    private DialogFragment dialogFragment;

    private boolean isDialogShowNeeded;

    private String tag;

    private boolean isDialogDismissNeeded;

    private boolean isActivityOnForeground;

    public void showDialog(DialogFragment dialogFragment, String tag) {
        if (isActivityOnForeground) {
            if (dialogFragment != null && !dialogFragment.isAdded() && getFragmentManager().findFragmentByTag(tag) == null) {
                dialogFragment.show(getFragmentManager(), tag);
            }
        } else {
            isDialogShowNeeded = true;
            this.dialogFragment = dialogFragment;
            this.tag = tag;
        }
    }

    public void dismissDialog(DialogFragment dialogFragment) {
        if (isActivityOnForeground) {
            if (dialogFragment != null && dialogFragment.getFragmentManager() != null) {
                dialogFragment.dismiss();
            }
        } else {
            isDialogDismissNeeded = true;
            this.dialogFragment = dialogFragment;
        }
    }

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

    protected void registerActionsListener() {

    }

    protected void addActionListener(String actionName) {
        serviceHelper.addActionListener(actionName, serviceBinder);
    }

    protected void removeActionListener(String actionName) {
        serviceHelper.removeActionListener(actionName);
    }

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

    protected void unregisterActionsListener() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        isActivityOnForeground = true;
        if (isDialogShowNeeded) {
            showDialog(dialogFragment, tag);
            isDialogShowNeeded = false;
        }
        if (isDialogDismissNeeded) {
            dismissDialog(dialogFragment);
            isDialogDismissNeeded = false;
        }
        serviceBinder.onResume();
        facebookUiHelper.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnForeground = false;
        serviceBinder.onPause();
        facebookUiHelper.onPause();
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
        return Arrays.<Object>asList(new ActivityModule(this));
    }

    public void showErrorToast(Bundle data) {
        if (data != null) {
            ErrorDto errorDto = (ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA);
            if (errorDto != null && !TextUtils.isEmpty(errorDto.getErrorMessage())) {
                ContextUtils.showToast(this, errorDto.getErrorMessage());
            }
        }
    }

    public void startProgress() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void stopProgress() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void processServerResult(String action, int resultCode, Bundle data) {

    }
}
