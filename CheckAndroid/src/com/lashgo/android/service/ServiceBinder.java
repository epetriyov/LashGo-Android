package com.lashgo.android.service;

import android.os.Bundle;

import java.util.ArrayList;
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
        serviceReceiver.inject(this);
    }

    private void deliverServiceResults() {
        for (ServiceResult serviceResult : serviceResultList) {
            onCommandFinished(serviceResult);
        }
    }

    private void onCommandFinished(ServiceResult serviceResult) {
        serviceReceiver.processServerResult(serviceResult.getAction(), serviceResult.getResultCode(), serviceResult.getData());
        serviceResultList.remove(serviceResult);
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
