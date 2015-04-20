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
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.social.VkontakteListener;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
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

import java.io.IOException;

/**
 * Created by Eugene on 17.06.2014.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, AuthController.AuthListener, DrawerController.DrawerOnClickListener {
    private TwitterHelper twitterHelper;

    private VkontakteListener vkSdkListener;

    private CallbackManager callbackManager;

    private DrawerController drawerController;

    private View drawerLoginMenu;

    private View drawerAuthMenu;

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
    private View drawerMenu;
    private View drawerTopView;
    private int avaSize;
    private int position;

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
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
            drawerController.updateCounters(mainScreenInfoDto);
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
        VKUIHelper.onResume(this);
        checkPlayServices();
        if (settingsHelper.isLoggedIn()) {
            serviceHelper.getMainScreenInfo(settingsHelper.getLastNewsView(), settingsHelper.getLastSubscriptionsView());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterHelper = new TwitterHelper(this);
        vkSdkListener = new VkontakteListener(this);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_main);
        callbackManager = CallbackManager.Factory.create();
        VKSdk.initialize(vkSdkListener, getString(R.string.vkontakte_app_id), null);
        twitterHelper.onCreate(savedInstanceState);
        authController = new AuthController(this, serviceHelper, callbackManager, twitterHelper, this);
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
        drawerController = new DrawerController((ViewGroup) drawerAuthMenu.findViewById(R.id.root_drawer_view), this);
        drawerController.init();
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
        drawerController.selectItem(position);
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
        if (settingsHelper.isLoggedIn() && (v.getId() == R.id.drawer_text || v.getId() == R.id.drawer_ava)) {
            startActivity(ProfileActivity.buildIntent(this, settingsHelper.getUserId()));
        }
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
            BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                fragment.refresh();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public void onClick(int position, Fragment fragment) {
        this.position = position;
        showFragment(fragment);
        setTitle(menuItems[position]);
        drawerLayout.closeDrawer(drawerMenu);
    }
}
