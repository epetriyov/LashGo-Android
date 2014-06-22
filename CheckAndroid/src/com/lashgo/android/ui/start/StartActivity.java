package com.lashgo.android.ui.start;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.service.handlers.GetLastCheckHandler;
import com.lashgo.android.service.handlers.RestHandlerFactory;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.GcmRegistrationDto;
import org.holoeverywhere.widget.Toast;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Eugene on 02.03.14.
 */
public class StartActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Tag used on log messages.
     */
    private static final String TAG = "GCM Demo";

    private GoogleCloudMessaging gcm;

    @Inject
    protected Handler handler;

    @Override
    protected void registerActionsListener() {
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_GCM_REGISTER_ID, this);
    }

    @Override
    protected void unregisterActionsListener() {
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_GCM_REGISTER_ID);
    }

    @Override
    protected void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (action.equals(RestHandlerFactory.ACTION_GCM_REGISTER_ID)) {
                if (settingsHelper.isLoggedIn()) {
                    if (settingsHelper.isFirstLaunch()) {
                        serviceHelper.getLastCheck();
                    } else {
                        startActivity(new Intent(MainActivity.buildIntent(this, null)));
                    }
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            } else if (action.equals(RestHandlerFactory.ACTION_GET_LAST_CHECK)) {
                startActivity(new Intent(MainActivity.buildIntent(this, (CheckDto) data.getSerializable(GetLastCheckHandler.CHECK_DTO))));
            }
        } else {
            Toast.makeText(this, data.getString(BaseIntentHandler.ERROR_EXTRA), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LashgoApplication.getInstance().getApplicationGraph().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_start);
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            String regid = settingsHelper.getRegistrationId();
            if (TextUtils.isEmpty(regid)) {
                registerInBackground();
            } else {
                serviceHelper.gcmRegisterId(new GcmRegistrationDto(regid));
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
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
                Log.i(TAG, "This device is not supported.");
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
                        gcm = GoogleCloudMessaging.getInstance(StartActivity.this);
                    }
                    regId = gcm.register(LashgoConfig.GCM_API_KEY);
                    // Persist the regID - no need to register again.
                    settingsHelper.saveRegistrationId(regId);
                } catch (IOException ex) {
                    Toast.makeText(StartActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return regId;
            }

            @Override
            protected void onPostExecute(String regId) {
                // You should send the registration ID to your server over HTTP, so it
                // can use GCM/HTTP or CCS to send messages to your app.
                sendRegistrationIdToBackend(regId);
            }
        }.execute(null, null, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(final String registrationId) {
        serviceHelper.gcmRegisterId(new GcmRegistrationDto(registrationId));
    }
}
