package com.lashgo.android.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.crittercism.app.Crittercism;
import com.lashgo.android.*;
import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.ServiceReceiver;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.search.SearchActivity;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.SocialInfo;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public abstract class BaseActivity extends FragmentActivity implements ServiceReceiver, View.OnClickListener {

    protected static final String PROGRESS_DIALOG = "progress";
    private static final int DISPLAY_RELAYOUT_MASK =
            ActionBar.DISPLAY_SHOW_HOME |
                    ActionBar.DISPLAY_USE_LOGO |
                    ActionBar.DISPLAY_HOME_AS_UP |
                    ActionBar.DISPLAY_SHOW_CUSTOM |
                    ActionBar.DISPLAY_SHOW_TITLE;
    @Inject
    protected ServiceHelper serviceHelper;
    @Inject
    protected SettingsHelper settingsHelper;
    @Inject
    protected Handler handler;
    @Inject
    ServiceBinder serviceBinder;
    protected MenuItem exitMenu;
    private View customActionBarView;
    private View homeBtn;
    private TextView actionBarTitle;
    private ObjectGraph loginGraph;
    private DialogFragment dialogFragment;
    private boolean isDialogShowNeeded;
    private String tag;
    private boolean isDialogDismissNeeded;
    private boolean isActivityOnForeground;
    private View progressView;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_home) {
            finish();
        }
    }

    public void showDialog(DialogFragment dialogFragment, String tag) {
        if (isActivityOnForeground) {
            if (dialogFragment != null && !dialogFragment.isAdded() && getSupportFragmentManager().findFragmentByTag(tag) == null) {
                dialogFragment.show(getSupportFragmentManager(), tag);
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
        if (!BuildConfig.DEBUG) {
            Crittercism.initialize(getApplicationContext(), LashgoConfig.CRITTERCISM_APP_ID);
        }
        loginGraph = LashgoApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        loginGraph.inject(this);
        super.onCreate(savedInstanceState);
        registerActionsListener();
    }

    protected void registerActionsListener() {

    }

    protected void addActionListener(String actionName) {
        serviceHelper.addActionListener(actionName, serviceBinder);
    }

    protected void removeActionListener(String actionName) {
        serviceHelper.removeActionListener(actionName, serviceBinder);
    }

    @Override
    protected void onDestroy() {
        loginGraph = null;
        super.onDestroy();
        unregisterActionsListener();
    }

    protected void unregisterActionsListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerActionsListener();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnForeground = false;
        serviceBinder.onPause();
    }

    public void onDisplayError(final String errorMessage) {
        runOnUiThread(new Runnable() {
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

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        progressView = findViewById(R.id.progress_view);
        if(progressView != null) {
            progressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    protected void hideOverlayProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    protected void showOverlayProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void setScreenTitle(int titleId) {
        actionBarTitle.setText(titleId);
    }

    protected void initCustomActionBar(int displayOptions) {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        customActionBarView = getLayoutInflater().inflate(R.layout.view_action_bar, null);
        homeBtn = customActionBarView.findViewById(R.id.action_home);
        homeBtn.setOnClickListener(this);
        actionBarTitle = ((TextView) customActionBarView.findViewById(R.id.action_title));
        actionBarTitle.setText(getTitle());
        getActionBar().setCustomView(customActionBarView, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.LEFT));
        applyDisplayOptions(displayOptions);
    }

    public void startProgress() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void stopProgress() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void processServerResult(String action, int resultCode, Bundle data) {
    }

    private void applyDisplayOptions(int options) {
        if ((-1 & DISPLAY_RELAYOUT_MASK) != 0) {
            if ((-1 & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
                final boolean setUp = (options & ActionBar.DISPLAY_HOME_AS_UP) != 0;
                homeBtn.setVisibility(setUp ? View.VISIBLE : View.GONE);
            }
            if ((-1 & ActionBar.DISPLAY_SHOW_TITLE) != 0) {
                final boolean setTitle = (options & ActionBar.DISPLAY_SHOW_TITLE) != 0;
                actionBarTitle.setVisibility(setTitle ? View.VISIBLE : View.GONE);
            }
            customActionBarView.requestLayout();
        }
    }

    public static enum ExtraNames {
        CHECK_DTO, PHOTO_URL, PROFILE_OWNER, USER_ID, PHOTO_DTO,
        PHOTO_TYPE, USER_DTO, CHECK_ID, PHOTO_ID, FROM, REQUEST_TOKEN,
        TWITTER_URL, CHECK_LIST, OPEN_MODE, PHOTOS_LIST,
        SELECTED_PHOTO, ACTIVITY_REFERRER, LOAD_ON_START, SEARCH_TEXT,
        VOTE_PHOTOS, POSITION, SIZE, NEW_NEWS_COUNT, SUBSCRIPTION_EVENTS, VOTE_DONE, IMAGE_PATH, TEMP_IMG_PATH;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        exitMenu = menu.findItem(R.id.action_exit);
        exitMenu.setVisible(settingsHelper.isLoggedIn());
        ImageView searchView = (ImageView) menu.findItem(R.id.action_search).getActionView();
        searchView.setImageResource(R.drawable.ic_action_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivity.this, SearchActivity.class));
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            logout();
            settingsHelper.logout();
        } else if (item.getItemId() == R.id.action_refresh) {
            refresh();
        } else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void refresh();

    public abstract void logout();
}
