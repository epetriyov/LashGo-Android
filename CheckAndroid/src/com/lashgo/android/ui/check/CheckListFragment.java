package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.adapters.MultyTypeAdapter;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.ResponseList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by Eugene on 19.06.2014.
 */
public class CheckListFragment extends BaseFragment implements AdapterView.OnItemClickListener, CheckItemBinder.OnCheckStateChangeListener {

    public static enum StartOptions {
        LOAD_ON_START, DONT_LOAD_ON_START
    }

    private ListView checkListView;

    private MultyTypeAdapter multyTypeAdapter;

    private Collection<CheckDto> resultCollection;

    private StartOptions loadOnStart;

    private String searchText;

    public CheckListFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(BaseActivity.ExtraNames.SEARCH_TEXT.name());
            loadOnStart = (StartOptions) savedInstanceState.getSerializable(BaseActivity.ExtraNames.LOAD_ON_START.name());
        } else {
            Bundle args = getArguments();
            if (args != null) {
                searchText = args.getString(BaseActivity.ExtraNames.SEARCH_TEXT.name());
                loadOnStart = (StartOptions) args.getSerializable(BaseActivity.ExtraNames.LOAD_ON_START.name());
            }
        }
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BaseActivity.ExtraNames.SEARCH_TEXT.name(), searchText);
        outState.putSerializable(BaseActivity.ExtraNames.LOAD_ON_START.name(), loadOnStart);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        checkListView = (ListView) inflater.inflate(R.layout.frag_check_list, container, false);
        multyTypeAdapter = new MultyTypeAdapter();
        multyTypeAdapter.addBinder(R.layout.adt_check_item, new CheckItemBinder(getActivity(), this));
        multyTypeAdapter.addBinder(R.layout.adt_check_state, new CheckStateBinder(getActivity()));
        checkListView.setAdapter(multyTypeAdapter);
        checkListView.setOnItemClickListener(this);
        getActivity().setTitle(R.string.check_list);
        if (StartOptions.LOAD_ON_START.equals(loadOnStart) || !TextUtils.isEmpty(searchText)) {
            refresh();
        }
        return checkListView;
    }

    @Override
    public void refresh() {
        serviceHelper.getChecks(searchText);
    }

    @Override
    protected void registerActionsListener() {
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_LIST.name());
    }

    @Override
    protected void unregisterActionsListener() {
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_LIST.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
            ((BaseActivity) getActivity()).showErrorToast(data);
        } else {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_LIST.name().equals(action) && data != null) {
                ResponseList<CheckDto> checkDtoResponseList = (ResponseList<CheckDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.KEY_CHECK_DTO_LIST.name());
                if (checkDtoResponseList != null) {
                    if (checkDtoResponseList.getResultCollection() != null) {
                        this.resultCollection = new ArrayList<>(checkDtoResponseList.getResultCollection());
                        onCheckListLoaded();
                    }
                }
            }
        }
    }

    private void onCheckListLoaded() {
        if (resultCollection != null) {
            multyTypeAdapter.clear();
            Calendar checkActiveCalendar = Calendar.getInstance();
            Calendar checkVoteCalendar = Calendar.getInstance();
            boolean isFirstIteration = true;
            boolean isLastActive = false;
            boolean isLastVote = false;
            String checkStatus;
            for (CheckDto checkDto : resultCollection) {
                checkActiveCalendar.setTime(checkDto.getStartDate());
                checkVoteCalendar.setTime(checkDto.getStartDate());
                checkActiveCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration());
                checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
                if (isFirstIteration ||
                        (isLastActive && (checkActiveCalendar.getTimeInMillis() <= System.currentTimeMillis())) ||
                        (isLastVote && (checkVoteCalendar.getTimeInMillis() < System.currentTimeMillis()))) {
                    /**
                     * check state differs from previous
                     */
                    if ((checkActiveCalendar.getTimeInMillis() > System.currentTimeMillis())) {
                        checkStatus = getString(R.string.active_checks);
                    } else if (checkVoteCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                        checkStatus = getString(R.string.vote_checks);
                    } else {
                        checkStatus = getString(R.string.finished_checks);
                    }
                    multyTypeAdapter.addItem(checkStatus, R.layout.adt_check_state, false);
                }
                multyTypeAdapter.addItem(checkDto, R.layout.adt_check_item, true);
                if (checkActiveCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                    isLastActive = true;
                } else {
                    if (checkVoteCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                        isLastVote = true;
                    } else {
                        isLastVote = false;
                    }
                    isLastActive = false;
                }
                isFirstIteration = false;
            }
            multyTypeAdapter.notifyDataSetChanged();
        }
    }

    public static Fragment newInstance(StartOptions loadOnStart) {
        Fragment fragment = new CheckListFragment();
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.ExtraNames.LOAD_ON_START.name(), loadOnStart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selectedItem = multyTypeAdapter.getItem(position);
        if (selectedItem instanceof CheckDto) {
            CheckDto selectedCheck = (CheckDto) selectedItem;
            startActivity(CheckActivity.buildIntent(getActivity(), selectedCheck));
        } else {
            throw new IllegalStateException("Selected item is not CheckDto object");
        }
    }

    @Override
    public void onCheckStateChanged() {
        if (getActivity() != null && !isDetached()) {
            onCheckListLoaded();
        }
    }
}
