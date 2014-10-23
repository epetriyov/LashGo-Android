package com.lashgo.android.ui.activity;

import android.os.Bundle;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.act_activities, container, false);
        noResults = view.findViewById(R.id.no_result);
        timeline = view.findViewById(R.id.timeline);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ActivityAdapter(getActivity());
        listView.setAdapter(adapter);
        serviceHelper.getEvents();
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
        serviceHelper.getEvents();
    }
}
