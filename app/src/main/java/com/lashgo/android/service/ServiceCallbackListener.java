package com.lashgo.android.service;

import android.os.Bundle;

/**
 * User: eugene.petriyov
 * Date: 25.06.13
 * Time: 13:32
 */
public interface ServiceCallbackListener {
    void onCommandFinished(String actionName, int resultCode, Bundle data);

    void onCommandStarted();

}