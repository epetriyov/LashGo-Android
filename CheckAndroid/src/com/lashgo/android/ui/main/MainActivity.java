package com.lashgo.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.activity.ActivityFragment;
import com.lashgo.android.ui.auth.AuthController;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.auth.SuccessfulRegisterActivity;
import com.lashgo.android.ui.check.CheckListFragment;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.ui.views.RobotoButton;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.GcmRegistrationDto;
import com.lashgo.model.dto.MainScreenInfoDto;
import com.lashgo.model.dto.UserDto;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Eugene on 17.06.2014.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, AuthController.AuthListener {

    @Inject
    protected UiLifecycleHelper facebookUiHelper;
    @Inject
    protected TwitterHelper twitterHelper;
    @Inject
    protected VkontakteListener vkSdkListener;
    @Inject
    protected FacebookHelper facebookHelper;

    private View drawerLoginMenu;

    private View drawerAuthMenu;

    private int subscribesCount;

    private String[] menuItems;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private AuthController authController;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleCloudMessaging gcm;
    private ImageView userAvatarView;
    private RobotoButton userName;
    private View itemTasks;
    private TextView tasksCountView;
    private TextView newsCountView;
    private TextView subscribesCountView;
    private View drawerMenu;
    private ImageView taskCountBg;
    private ImageView newsCountBg;
    private ImageView subscribesCountBg;
    private View tasksCountRoot;
    private View newsCountRoot;
    private View subscribesCountRoot;
    private View drawerTopView;
    private int avaSize;

    private int position;
    private View itemEvents;
    private View itemNews;

    @Override
    protected void registerActionsListener() {
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GCM_REGISTER_ID.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUiHelper.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TwitterHelper.TWITTER_AUTH) {
            if (resultCode == Activity.RESULT_OK) {
                twitterHelper.handleCallbackUrl(data.getData());
            }
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        if (BaseIntentHandler.ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                MainScreenInfoDto mainScreenInfoDto = (MainScreenInfoDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.MAIN_SCREEN_INFO.name());
                updateMainScreenInfo(mainScreenInfoDto);
            } else {
                showErrorToast(data);
            }
        } else {
            authController.handleServerResponse(action, resultCode, data);
        }
    }

    private void updateMainScreenInfo(MainScreenInfoDto mainScreenInfoDto) {
        if (mainScreenInfoDto != null) {
            userName.setText(mainScreenInfoDto.getUserName());
            String userAvatar = mainScreenInfoDto.getUserAvatar();
            if (!TextUtils.isEmpty(userAvatar)) {
                PhotoUtils.displayImage(this, userAvatarView, LashGoUtils.getUserAvatarUrl(userAvatar), avaSize, R.drawable.ava, false);
            }
            int tasksCount = mainScreenInfoDto.getTasksCount();
            tasksCountView.setText(String.valueOf(tasksCount));
            if (tasksCount > 9) {
                taskCountBg.setImageResource(R.drawable.ic_notification_big);
            } else if (tasksCount > 0 && tasksCount <= 9) {
                taskCountBg.setImageResource(R.drawable.ic_notification_small);
            } else {
                tasksCountRoot.setVisibility(View.GONE);
            }

            int newsCount = mainScreenInfoDto.getNewsCount();
            newsCountView.setText(String.valueOf(newsCount));
            if (newsCount > 9) {
                newsCountBg.setImageResource(R.drawable.ic_notification_big);
            } else if (newsCount > 0 && newsCount <= 9) {
                newsCountBg.setImageResource(R.drawable.ic_notification_small);
            } else {
                newsCountRoot.setVisibility(View.GONE);
            }

            subscribesCount = mainScreenInfoDto.getSubscribesCount();
            subscribesCountView.setText(String.valueOf(subscribesCount));
            if (subscribesCount > 9) {
                subscribesCountBg.setImageResource(R.drawable.ic_notification_big);
            } else if (subscribesCount > 0 && subscribesCount <= 9) {
                subscribesCountBg.setImageResource(R.drawable.ic_notification_small);
            } else {
                subscribesCountRoot.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void unregisterActionsListener() {
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GCM_REGISTER_ID.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MAIN_SCREEN_INFO.name());
    }

    @Override
    protected void onResume() {
        super.onResume();
        facebookUiHelper.onResume();
        VKUIHelper.onResume(this);
        checkPlayServices();
        if (settingsHelper.isLoggedIn()) {
            serviceHelper.getMainScreenInfo(settingsHelper.getLastNewsView(), settingsHelper.getLastSubscriptionsView());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookUiHelper.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_main);
        facebookUiHelper.onCreate(savedInstanceState);
        VKSdk.initialize(vkSdkListener, getString(R.string.vkontakte_app_id), null);
        twitterHelper.onCreate(savedInstanceState);
        authController = new AuthController(this, serviceHelper, facebookHelper, twitterHelper, this);
        settingsHelper.setFirstLaunch();
        avaSize = PhotoUtils.convertDpToPixels(64, this);
        initViews();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        registerGcm();
    }

    private void registerGcm() {
        if (settingsHelper.isLoggedIn()) {
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                String regid = settingsHelper.getRegistrationId();
                if (TextUtils.isEmpty(regid)) {
                    registerInBackground();
                } else {
                    sendRegistrationIdToBackend(regid);
                }
            } else {
                ContextUtils.showToast(this, "No valid Google Play Services APK found.");
            }
        }
    }

    private void initViews() {
        title = drawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menus_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerMenu = findViewById(R.id.drawer_menu);
        drawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        drawerTopView = findViewById(R.id.drawer_top_view);
        userAvatarView = (ImageView) findViewById(R.id.drawer_ava);
        userAvatarView.setOnClickListener(this);
        userName = (RobotoButton) findViewById(R.id.drawer_text);
        userName.setOnClickListener(this);
        ViewStub drawerMenuStub = (ViewStub) findViewById(R.id.view_login_stub);
        drawerLoginMenu = drawerMenuStub.inflate();
        authController.initViews(drawerLoginMenu);
        ViewStub drawerAuthMenuStub = (ViewStub) findViewById(R.id.view_auth_stub);
        drawerAuthMenu = drawerAuthMenuStub.inflate();
        itemTasks = drawerAuthMenu.findViewById(R.id.item_tasks);
        itemTasks.setOnClickListener(this);
        itemNews = drawerAuthMenu.findViewById(R.id.item_news);
        itemEvents = drawerAuthMenu.findViewById(R.id.item_subscribes);
        taskCountBg = (ImageView) drawerAuthMenu.findViewById(R.id.tasks_count_bg);
        tasksCountRoot = drawerAuthMenu.findViewById(R.id.tasks_count);
        tasksCountView = (TextView) drawerAuthMenu.findViewById(R.id.tasks_count_value);
        drawerAuthMenu.findViewById(R.id.item_news).setOnClickListener(this);
        newsCountRoot = drawerAuthMenu.findViewById(R.id.news_count);
        newsCountView = (TextView) drawerAuthMenu.findViewById(R.id.news_count_value);
        newsCountBg = (ImageView) drawerAuthMenu.findViewById(R.id.news_count_bg);
        drawerAuthMenu.findViewById(R.id.item_subscribes).setOnClickListener(this);
        subscribesCountRoot = drawerAuthMenu.findViewById(R.id.subscribes_count);
        subscribesCountBg = (ImageView) drawerAuthMenu.findViewById(R.id.subscribes_count_bg);
        subscribesCountView = (TextView) drawerAuthMenu.findViewById(R.id.subscribes_count_value);
        updateDrawer();

        // Set the adapter for the list view
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.drawer_normal, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        facebookUiHelper.onSaveInstanceState(outState);
        outState.putSerializable(TwitterHelper.KEY_REQUEST_TOKEN, twitterHelper.getRequestToken());
        outState.putInt(ExtraNames.POSITION.name(), position);
        super.onSaveInstanceState(outState);
    }

    private void initNotAuthDrawerMenu() {
        if (exitMenu != null) {
            exitMenu.setVisible(false);
        }
        drawerTopView.setBackgroundColor(getResources().getColor(R.color.main_color));
        userName.setText(R.string.login_or_register);
        userAvatarView.setImageResource(R.drawable.ava);
        drawerLoginMenu.setVisibility(View.VISIBLE);
        if (drawerAuthMenu != null) {
            drawerAuthMenu.setVisibility(View.GONE);
        }
        showFragment(CheckListFragment.newInstance(CheckListFragment.StartOptions.LOAD_ON_START));
        setTitle(menuItems[0]);
        drawerLayout.closeDrawer(drawerMenu);
    }

    private void initAuthDrawerMenu() {
        if (exitMenu != null) {
            exitMenu.setVisible(true);
        }
        drawerTopView.setBackgroundResource(R.drawable.bg_navigation);
        drawerAuthMenu.setVisibility(View.VISIBLE);
        if (drawerLoginMenu != null) {
            drawerLoginMenu.setVisibility(View.GONE);
        }
        View view = null;
        switch (position) {
            case 0:
                view = itemTasks;
                break;
            case 1:
                view = itemNews;
                break;
            case 2:
                view = itemEvents;
                break;
            default:
                view = itemTasks;
                break;
        }
        selectItem(view);

    }

    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(ExtraNames.POSITION.name(), 0);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                ContextUtils.showToast(this, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String regId = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regId = gcm.register(LashgoConfig.GCM_API_KEY);
                    // Persist the regID - no need to register again.
                    settingsHelper.saveRegistrationId(regId);
                } catch (final IOException ex) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ContextUtils.showToast(MainActivity.this, ex.getMessage());
                        }
                    });
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return regId;
            }

            @Override
            protected void onPostExecute(String regId) {
                sendRegistrationIdToBackend(regId);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(final String registrationId) {
        serviceHelper.gcmRegisterId(new GcmRegistrationDto(registrationId));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_tasks || v.getId() == R.id.item_news || v.getId() == R.id.item_subscribes) {
            selectItem(v);
        } else if (settingsHelper.isLoggedIn() && (v.getId() == R.id.drawer_text || v.getId() == R.id.drawer_ava)) {
            startActivity(ProfileActivity.buildIntent(this, settingsHelper.getUserId()));
        }
    }


    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(View view) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        position = 0;
        if (view.getId() == R.id.item_tasks) {
            fragment = CheckListFragment.newInstance(CheckListFragment.StartOptions.LOAD_ON_START);
            position = 0;
        } else if (view.getId() == R.id.item_news) {
            fragment = ActivityFragment.newInstance(subscribesCount, false);
            position = 1;
        } else if (view.getId() == R.id.item_subscribes) {
            fragment = ActivityFragment.newInstance(subscribesCount, true);
            position = 2;
        }
        showFragment(fragment);

        setTitle(menuItems[position]);
        drawerLayout.closeDrawer(drawerMenu);
    }

    private void showFragment(Fragment fragment) {
        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(this.title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void refresh() {
        serviceHelper.getMainScreenInfo(settingsHelper.getLastNewsView(), settingsHelper.getLastSubscriptionsView());
        if (position == 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                ((CheckListFragment) fragment).refresh();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        facebookUiHelper.onPause();
    }

    @Override
    public void onLoginSuccessFull() {
        updateDrawer();
    }

    @Override
    public void onRegisterSuccessFull(UserDto userDto) {
        finish();
        startActivity(SuccessfulRegisterActivity.buildIntent(this, userDto, LoginActivity.OpenMode.FROM_CHECK));
    }

    private void updateDrawer() {
        if (settingsHelper.isLoggedIn()) {
            initAuthDrawerMenu();
            serviceHelper.getMainScreenInfo(settingsHelper.getLastNewsView(), settingsHelper.getLastSubscriptionsView());
        } else {
            initNotAuthDrawerMenu();
        }
    }

    public void logout() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDrawer();
            }
        }, 100l);

    }
}
