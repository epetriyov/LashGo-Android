package com.lashgo.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.android.FragmentModule;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.service.ServiceBinder;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.ServiceReceiver;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 15.07.2014.
 */
public class BaseFragment extends Fragment implements ServiceReceiver {

    @Inject
    ServiceBinder serviceBinder;

    @Inject
    protected ServiceHelper serviceHelper;

    private ObjectGraph fragmentGraph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentGraph = LashgoApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        fragmentGraph.inject(this);
        registerActionsListener();
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    protected void registerActionsListener() {

    }

    protected void addActionListener(String actionName) {
        serviceHelper.addActionListener(actionName, serviceBinder);
    }

    protected void removeActionListener(String actionName) {
        serviceHelper.removeActionListener(actionName);
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
        serviceBinder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceBinder.onPause();
    }

    public void processServerResult(String action, int resultCode, Bundle data) {

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
}
