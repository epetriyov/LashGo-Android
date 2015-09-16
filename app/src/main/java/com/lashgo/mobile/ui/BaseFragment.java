package com.lashgo.mobile.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lashgo.mobile.LashgoApplication;
import com.lashgo.mobile.R;
import com.lashgo.mobile.service.ServiceBinder;
import com.lashgo.mobile.service.ServiceHelper;
import com.lashgo.mobile.service.ServiceReceiver;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.settings.SettingsHelper;

/**
 * Created by Eugene on 15.07.2014.
 */
public abstract class BaseFragment extends Fragment implements ServiceReceiver {

    protected ServiceBinder serviceBinder;

    protected ServiceHelper serviceHelper;

    protected SettingsHelper settingsHelper;

    private View progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsHelper = LashgoApplication.getInstance().getSettingsHelper();
        serviceHelper = LashgoApplication.getInstance().getServiceHelper();
        serviceBinder = new ServiceBinder(this);
        registerActionsListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public abstract void refresh();

    protected void registerActionsListener() {

    }

    protected void addActionListener(String actionName) {
        serviceHelper.addActionListener(actionName, serviceBinder);
    }

    protected void removeActionListener(String actionName) {
        serviceHelper.removeActionListener(actionName, serviceBinder);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterActionsListener();
    }

    protected void unregisterActionsListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        registerActionsListener();
        serviceBinder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceBinder.onPause();
    }

    public void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
            ((BaseActivity) getActivity()).showErrorToast(data);
        }
    }

    @Override
    public void stopProgress() {
        ((BaseActivity) getActivity()).stopProgress();
    }

    @Override
    public void startProgress() {
        ((BaseActivity) getActivity()).startProgress();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressView = view.findViewById(R.id.progress_view);
        if (progressView != null) {
            progressView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    protected void hideOverlayProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    protected void showOverlayProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }
}
