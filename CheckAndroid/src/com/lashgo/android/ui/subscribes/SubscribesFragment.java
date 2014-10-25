package com.lashgo.android.ui.subscribes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.model.dto.SubscribeDto;
import com.lashgo.model.dto.SubscriptionDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 19.06.2014.
 */
public class SubscribesFragment extends BaseFragment implements AdapterView.OnItemClickListener, SubscriptionAdapter.ActionBtnClickListener {

    public static Fragment newInstance(int objectId, ScreenType screenType, String searchText) {
        Bundle args = new Bundle();
        args.putSerializable(SCREEN_TYPE, screenType);
        args.putInt(BaseActivity.ExtraNames.USER_ID.name(), objectId);
        args.putString(BaseActivity.ExtraNames.SEARCH_TEXT.name(), searchText);
        SubscribesFragment fragment = new SubscribesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static enum ScreenType {
        SUBSCRIPTIONS, SUBSCRIBERS, CHECK_USERS, SEARCH_USERS
    }

    public static final String SCREEN_TYPE = "screen_type";
    private SubscriptionAdapter adapter;
    private ScreenType screenType;
    private View noResult;
    private int position;
    private int objectId;
    private String searchText;

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        refresh();
    }

    public static SubscribesFragment newInstance(int objectId, ScreenType screenType) {
        Bundle args = new Bundle();
        args.putSerializable(SCREEN_TYPE, screenType);
        args.putInt(BaseActivity.ExtraNames.USER_ID.name(), objectId);
        SubscribesFragment fragment = new SubscribesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_TYPE, screenType);
        outState.putInt(BaseActivity.ExtraNames.USER_ID.name(), objectId);
        outState.putString(BaseActivity.ExtraNames.SEARCH_TEXT.name(), searchText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (savedInstanceState != null) {
            screenType = (ScreenType) savedInstanceState.getSerializable(SCREEN_TYPE);
            objectId = savedInstanceState.getInt(BaseActivity.ExtraNames.USER_ID.name());
            searchText = savedInstanceState.getString(BaseActivity.ExtraNames.SEARCH_TEXT.name());
        } else if (args != null) {
            screenType = (ScreenType) args.getSerializable(SCREEN_TYPE);
            objectId = args.getInt(BaseActivity.ExtraNames.USER_ID.name());
            searchText = args.getString(BaseActivity.ExtraNames.SEARCH_TEXT.name());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_check_list, container, false);
        noResult = rootView.findViewById(R.id.no_result);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new SubscriptionAdapter(getActivity(), this, settingsHelper);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        updateSubscribes();
        return rootView;
    }

    private void updateSubscribes() {
        if (ScreenType.SUBSCRIBERS.name().equals(screenType.name())) {
            serviceHelper.getSubscribers(objectId);
        } else if (ScreenType.SUBSCRIPTIONS.name().equals(screenType.name())) {
            serviceHelper.getSubscriptions(objectId);
        } else if (ScreenType.CHECK_USERS.name().equals(screenType.name())) {
            serviceHelper.getCheckUsers(objectId);
        } else {
            serviceHelper.searchUsers(searchText);
        }
    }

    @Override
    public void refresh() {
        updateSubscribes();
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (BaseIntentHandler.SUCCESS_RESPONSE == resultCode) {
            if (action.equals(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIPTIONS.name())
                    || action.equals(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIBERS.name())
                    || action.equals(BaseIntentHandler.ServiceActionNames.ACTION_FIND_USERS.name())
                    || action.equals(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_USERS.name())) {
                if (data != null) {
                    ArrayList<SubscriptionDto> subscriptionDtos = (ArrayList<SubscriptionDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.USERS_DTO.name());
                    updateAdapter(subscriptionDtos);
                }
            } else if (action.equals(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name()) || action.equals(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name())) {
                if (screenType.name().equals(ScreenType.SUBSCRIPTIONS) && action.equals(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name())) {
                    adapter.remove(adapter.getItem(position));
                } else {
                    adapter.getItem(position).setAmISubscribed(!adapter.getItem(position).isAmISubscribed());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void updateAdapter(ArrayList<SubscriptionDto> subscriptionDtos) {
        if (subscriptionDtos != null) {
            adapter.clear();
            for (SubscriptionDto subscriptionDto : subscriptionDtos) {
                adapter.add(subscriptionDto);
            }
            adapter.notifyDataSetChanged();
        } else {
            noResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIPTIONS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_USERS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIBERS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_FIND_USERS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIPTIONS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_USERS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_SUBSCRIBERS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_FIND_USERS.name());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(ProfileActivity.buildIntent(getActivity(), adapter.getItem(i).getUserId()));
    }

    @Override
    public void onActionBtnClicked(int position) {
        this.position = position;
        SubscriptionDto subscriptionDto = adapter.getItem(position);
        if (subscriptionDto.isAmISubscribed()) {
            serviceHelper.unsubscribe(subscriptionDto.getUserId());
        } else {
            serviceHelper.subscribe(new SubscribeDto(subscriptionDto.getUserId()));
        }
    }
}
