package com.lashgo.mobile.service;

import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 28.02.14
 * Time: 0:14
 * To change this template use File | Settings | File Templates.
 */
public class ServiceResult {

    public int getResultCode() {
        return resultCode;
    }

    public Bundle getData() {
        return data;
    }

    public String getAction() {
        return action;
    }

    private String action;
    private int resultCode;
    private Bundle data;

    public ServiceResult(String action, int resultCode, Bundle data) {
        this.action = action;
        this.resultCode = resultCode;
        this.data = data;
    }
}
