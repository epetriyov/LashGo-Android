package com.check.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.check.android.CheckApplication;
import com.check.android.service.handlers.RestHandlerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class CheckService extends IntentService {

    public static final String EXTRA_STATUS_RECEIVER = "status_receiver";
    private RestService restService;

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
        restService = CheckApplication.getInstance().getService();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
            RestService service = CheckApplication.getInstance().getService();
            RestHandlerFactory.getIntentHandler(getApplicationContext(), action,
                    service).execute(intent, receiver);
        }
    }
}
