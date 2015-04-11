package com.lashgo.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.android.FragmentModule;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.ServiceReceiver;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.settings.SettingsHelper;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 15.07.2014.
 */
public abstract class BaseFragment extends Fragment implements ServiceReceiver {

    @Inject
    ServiceBinder serviceBinder;

    @Inject
    protected ServiceHelper serviceHelper;

    @Inject
    protected SettingsHelper settingsHelper;

    private ObjectGraph fragmentGraph;
    private View progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentGraph = LashgoApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        fragmentGraph.inject(this);
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
        fragmentGraph = null;
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

    public void inject(Object object) {
        fragmentGraph.inject(object);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new FragmentModule(this));
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
