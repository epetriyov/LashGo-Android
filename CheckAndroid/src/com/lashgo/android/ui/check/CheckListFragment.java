package com.lashgo.android.ui.check;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.service.handlers.GetCheckListHandler;
import com.lashgo.android.service.handlers.RestHandlerFactory;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.adapters.MultyTypeAdapter;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.ResponseList;

import java.util.Calendar;
import java.util.Collection;

/**
 * Created by Eugene on 19.06.2014.
 */
public class CheckListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView checkListView;

    private MultyTypeAdapter multyTypeAdapter;

    public CheckListFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        checkListView = (ListView) inflater.inflate(R.layout.frag_check_list, container, false);
        multyTypeAdapter = new MultyTypeAdapter();
        multyTypeAdapter.addBinder(R.layout.adt_check_item, new CheckItemBinder(getActivity()));
        multyTypeAdapter.addBinder(R.layout.adt_check_state, new CheckStateBinder(getActivity()));
        checkListView.setAdapter(multyTypeAdapter);
        checkListView.setOnItemClickListener(this);
        getActivity().setTitle(R.string.check_list);
        serviceHelper.getChecks();
        return checkListView;
    }

    @Override
    protected void registerActionsListener() {
        addActionListener(RestHandlerFactory.ACTION_GET_CHECK_LIST);
    }

    @Override
    protected void unregisterActionsListener() {
        removeActionListener(RestHandlerFactory.ACTION_GET_CHECK_LIST);
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        if (RestHandlerFactory.ACTION_GET_CHECK_LIST.equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                ResponseList<CheckDto> checkDtoResponseList = (ResponseList<CheckDto>) data.getSerializable(GetCheckListHandler.KEY_CHECK_DTO_LIST);
                if (checkDtoResponseList != null) {
                    onCheckListLoaded(checkDtoResponseList.getResultCollection());
                }
            }
        }
    }

    private void onCheckListLoaded(Collection<CheckDto> resultCollection) {
        if (resultCollection != null) {
            Calendar calendar = Calendar.getInstance();
            boolean isFirstIteration = true;
            boolean isLastActive = false;
            String checkStatus;
            for (CheckDto checkDto : resultCollection) {
                calendar.setTime(checkDto.getStartDate());
                calendar.add(Calendar.HOUR, checkDto.getDuration());
                if (isFirstIteration || (isLastActive && (calendar.getTimeInMillis() > System.currentTimeMillis()))) {
                    if ((calendar.getTimeInMillis() > System.currentTimeMillis())) {
                        checkStatus = getString(R.string.finished_checks);
                    } else {
                        checkStatus = getString(R.string.active_checks);
                    }
                    multyTypeAdapter.addItem(checkStatus, R.layout.adt_check_state, false);
                }
                multyTypeAdapter.addItem(checkDto, R.layout.adt_check_item, true);
                if ((calendar.getTimeInMillis() <= System.currentTimeMillis())) {
                    isLastActive = true;
                } else {
                    isLastActive = false;
                }
                isFirstIteration = false;
            }
            multyTypeAdapter.notifyDataSetChanged();
        }
    }

    public static Fragment newInstance() {
        return new CheckListFragment();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
