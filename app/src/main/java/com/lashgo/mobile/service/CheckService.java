package com.lashgo.mobile.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.lashgo.mobile.service.handlers.BaseIntentHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class CheckService extends IntentService {

    public static final String EXTRA_STATUS_RECEIVER = "status_receiver";

    public CheckService() {
        super("CheckService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CheckService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
            BaseIntentHandler.getIntentHandler(this,action).execute(intent, receiver);
        }
    }
}
