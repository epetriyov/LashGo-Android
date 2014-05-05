package com.check.android.ui.start;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.check.android.CheckApplication;
import com.check.android.CheckConfig;
import com.check.android.R;
import com.check.android.service.handlers.RestHandlerFactory;
import com.check.android.ui.BaseActivity;
import com.check.model.dto.GcmRegistrationDto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.holoeverywhere.widget.Toast;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 02.03.14
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        CheckApplication.getInstance().getApplicationGraph().inject(this);
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
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(StartActivity.this);
                    }
                    String regid = gcm.register(CheckConfig.GCM_API_KEY);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(regid);
                    // Persist the regID - no need to register again.
                    settingsHelper.saveRegistrationId(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(StartActivity.this, msg, Toast.LENGTH_LONG).show();
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                serviceHelper.gcmRegisterId(new GcmRegistrationDto(registrationId));
            }
        });
    }
}
