package com.check.android.service;

import android.app.IntentService;
import android.content.Intent;
import com.check.android.CheckApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 24.02.14
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class CheckService extends IntentService {

    private RestService restService;

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

    }
}
