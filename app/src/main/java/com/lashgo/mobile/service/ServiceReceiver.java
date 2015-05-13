package com.lashgo.mobile.service;

import android.os.Bundle;

/**
 * Created by Eugene on 14.07.2014.
 */
public interface ServiceReceiver {

    void processServerResult(String action, int resultCode, Bundle data);

    void stopProgress();

    void startProgress();
}
