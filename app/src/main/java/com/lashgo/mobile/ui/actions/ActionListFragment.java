package com.lashgo.mobile.ui.actions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.mobile.R;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.ui.BaseFragment;
import com.lashgo.mobile.ui.check.CheckListFragment;
import com.lashgo.mobile.ui.views.CustomViewPager;
import com.lashgo.mobile.utils.LashGoUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.ResponseList;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 18.04.2015.
 */
public class ActionListFragment extends BaseFragment {

    private CustomViewPager viewPager;
    private ActionsPagerAdapter pagerAdapter;
    private TabPageIndicator tabPageIndicator;

    @Override
    public void refresh() {
        serviceHelper.getActions(null);
    }

    public static Fragment newInstance() {
        return new ActionListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_ACTIONS_LIST.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_ACTIONS_LIST.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE && data != null) {
            ResponseList<CheckDto> checkDtoResponseList = (ResponseList<CheckDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.KEY_CHECK_DTO_LIST.name());
            if (checkDtoResponseList != null) {
                if (checkDtoResponseList.getResultCollection() != null) {
                    onCheckListLoaded(checkDtoResponseList.getResultCollection());
                }
            }
        }
    }

    private void onCheckListLoaded(ArrayList<CheckDto> resultCollection) {
        List<CheckDto> activeChecks = new ArrayList<>();
        List<CheckDto> finishedChecks = new ArrayList<>();
        for (CheckDto checkDto : resultCollection) {
            if (checkDto.getStartDate().getTime()
                    + TimeUnit.HOURS.toMillis(checkDto.getDuration())
                    + TimeUnit.HOURS.toMillis(checkDto.getVoteDuration())
                    > System.currentTimeMillis()) {
                activeChecks.add(checkDto);
            } else if (checkDto.getUserPhotoDto() != null) {
                finishedChecks.add(checkDto);
            }
        }
        CheckListFragment fragment = (CheckListFragment) LashGoUtils.findFragmentByPosition(getChildFragmentManager(), viewPager, pagerAdapter, 0);
        if (fragment != null) {
            fragment.onCheckListLoaded(activeChecks);
        }
        fragment = (CheckListFragment) LashGoUtils.findFragmentByPosition(getChildFragmentManager(), viewPager, pagerAdapter, 1);
        if (fragment != null) {
            fragment.onCheckListLoaded(finishedChecks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_actions, container, false);
        viewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        pagerAdapter = new ActionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.titles);
        tabPageIndicator.setViewPager(viewPager);
        return view;
    }

    private class ActionsPagerAdapter extends FragmentPagerAdapter {

        private String[] content;

        public ActionsPagerAdapter(FragmentManager fm) {
            super(fm);
            content = new String[]{getString(R.string.active_checks), getString(R.string.finished_checks)};
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return CheckListFragment.newInstance(CheckListFragment.StartOptions.DONT_LOAD_ON_START);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return content[position % content.length].toUpperCase();
        }
    }
}
