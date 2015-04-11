package com.lashgo.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.model.dto.EventDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 21.10.2014.
 */
public class ActivityFragment extends BaseFragment {

    private View timeline;

    private ActivityAdapter adapter;

    private View noResults;
    private int subscibesCount;

    private boolean subscriptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.act_activities, container, false);
        noResults = view.findViewById(R.id.no_result);
        timeline = view.findViewById(R.id.timeline);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ActivityAdapter(getActivity(), subscibesCount, settingsHelper.getUserId());
        listView.setAdapter(adapter);
        serviceHelper.getEvents(subscriptions);
        return view;
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_EVENTS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_EVENTS.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).showErrorToast(data);
            }
        } else {
            if (data != null) {
                updateAdapter((ArrayList<EventDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.EVENTS_DTO.name()));
            }
        }

    }

    private void updateAdapter(ArrayList<EventDto> eventDtos) {
        if (eventDtos != null) {
            adapter.clear();
            for (EventDto eventDto : eventDtos) {
                adapter.add(eventDto);
            }
            adapter.notifyDataSetChanged();
        } else {
            timeline.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        serviceHelper.getEvents(subscriptions);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(BaseActivity.ExtraNames.NEW_NEWS_COUNT.name(), subscibesCount);
        outState.putBoolean(BaseActivity.ExtraNames.SUBSCRIPTION_EVENTS.name(),subscriptions);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (savedInstanceState != null) {
            subscibesCount = savedInstanceState.getInt(BaseActivity.ExtraNames.NEW_NEWS_COUNT.name());
            subscriptions = savedInstanceState.getBoolean(BaseActivity.ExtraNames.SUBSCRIPTION_EVENTS.name());
        } else if (args != null) {
            subscibesCount = args.getInt(BaseActivity.ExtraNames.NEW_NEWS_COUNT.name());
            subscriptions = args.getBoolean(BaseActivity.ExtraNames.SUBSCRIPTION_EVENTS.name());
        }
    }

    public static Fragment newInstance(int subscribesCount,boolean subscriptions) {
        Fragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putInt(BaseActivity.ExtraNames.NEW_NEWS_COUNT.name(), subscribesCount);
        args.putBoolean(BaseActivity.ExtraNames.SUBSCRIPTION_EVENTS.name(),subscriptions);
        fragment.setArguments(args);
        return fragment;
    }
}
