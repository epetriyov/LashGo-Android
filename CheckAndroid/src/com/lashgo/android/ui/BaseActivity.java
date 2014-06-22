package com.lashgo.android.ui;

import android.os.Bundle;
import android.view.Window;
import com.lashgo.android.service.ServiceCallbackListener;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.settings.SettingsHelper;
import org.holoeverywhere.app.Activity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public abstract class BaseActivity extends Activity implements ServiceCallbackListener {

    @Inject
    protected ServiceHelper serviceHelper;

    @Inject
    protected SettingsHelper settingsHelper;

    private boolean isActivityOnForeground;

    private List<ServiceResult> serviceResultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        registerActionsListener();
    }

    protected abstract void registerActionsListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterActionsListener();
    }

    protected abstract void unregisterActionsListener();

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOnForeground = true;
        deliverServiceResults();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnForeground = false;
    }

    private void deliverServiceResults() {
        for (ServiceResult serviceResult : serviceResultList) {
            onCommandFinished(serviceResult);
        }
    }

    @Override
    public void onCommandStarted() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    private void onCommandFinished(ServiceResult serviceResult) {
        processServerResult(serviceResult.getAction(), serviceResult.getResultCode(), serviceResult.getData());
        serviceResultList.remove(serviceResult);
    }

    protected abstract void processServerResult(String action, int resultCode, Bundle data);

    @Override
    public void onCommandFinished(String action, int resultCode, Bundle data) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (!isActivityOnForeground) {
            saveServiceResult(action, resultCode, data);
        } else {
            processServerResult(action, resultCode, data);
        }
    }

    private void saveServiceResult(String action, int resultCode, Bundle data) {
        serviceResultList.add(new ServiceResult(action, resultCode, data));
    }

}
