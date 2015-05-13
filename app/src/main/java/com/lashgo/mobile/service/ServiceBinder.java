package com.lashgo.mobile.service;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugene on 14.07.2014.
 */
public class ServiceBinder implements ServiceCallbackListener {

    ServiceReceiver serviceReceiver;

    private boolean isActivityOnForeground;

    private List<ServiceResult> serviceResultList = new ArrayList<>();

    public ServiceBinder(ServiceReceiver serviceReceiver) {
        this.serviceReceiver = serviceReceiver;
    }

    private void deliverServiceResults() {
        synchronized (this) {
            for (Iterator<ServiceResult> iter = serviceResultList.iterator(); iter.hasNext(); ) {
                ServiceResult serviceResult = iter.next();
                serviceReceiver.processServerResult(serviceResult.getAction(), serviceResult.getResultCode(), serviceResult.getData());
                iter.remove();
            }
        }
    }

    private void saveServiceResult(String action, int resultCode, Bundle data) {
        serviceResultList.add(new ServiceResult(action, resultCode, data));
    }

    public void onCommandFinished(String action, int resultCode, Bundle data) {
        serviceReceiver.stopProgress();
        if (!isActivityOnForeground) {
            saveServiceResult(action, resultCode, data);
        } else {
            serviceReceiver.processServerResult(action, resultCode, data);
        }
    }


    public void onCommandStarted() {
        serviceReceiver.startProgress();
    }

    public void onResume() {
        isActivityOnForeground = true;
        deliverServiceResults();
    }

    public void onPause() {
        isActivityOnForeground = false;
    }
}
