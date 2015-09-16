/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lashgo.mobile.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lashgo.mobile.R;
import com.lashgo.mobile.ui.check.CheckActivity;
import com.lashgo.mobile.ui.main.MainActivity;
import com.lashgo.model.GcmEventType;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    private static final String GCM_CHECK_NAME = "check_name";

    private static final String GCM_CHECK_ID = "check_id";

    private static final String ACTION_TYPE = "action_type";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!TextUtils.isEmpty(messageType)) {
            Log.d("Message type", messageType);
        }
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle bundle) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(CheckActivity.class);
        String checkId = bundle.getString(GCM_CHECK_ID);
        try {
            int checkIntId = Integer.parseInt(checkId);
            stackBuilder.addNextIntent(CheckActivity.buildIntent(this, checkIntId));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        }

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        String checkName = bundle.getString(GCM_CHECK_NAME);
        String actionType = bundle.getString(ACTION_TYPE);
        int contentTitle = -1;
        if (actionType != null) {
            if (actionType.equals(GcmEventType.VOTE_STARTED.name())) {
                contentTitle = R.string.vote_started;
            } else if (actionType.equals(GcmEventType.CHECK_STARTED.name())) {
                contentTitle = R.string.notification_title;
            } else {
                contentTitle = R.string.finished_check;
            }
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo);
        if (contentTitle > 0) {
            mBuilder.setContentTitle(getResources().getString(contentTitle));
        }
        mBuilder.setContentText(checkName != null ? checkName : "");

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setVibrate(null);
        mBuilder.setSound(null);
        mBuilder.setDefaults(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.getNotification());
        }

    }
}
