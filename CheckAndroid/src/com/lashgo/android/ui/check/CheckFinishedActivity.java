package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckFinishedActivity extends CheckBaseActivity {

    private List<Fragment> fragmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_finished);
        fragmentsList = new ArrayList<>(2);
        fragmentsList.add(CheckFinishedFragment.newInstance(checkDto));
        fragmentsList.add(CheckPhotosFragment.newInstance(checkDto));
        ViewPager viewPager = (ViewPager) findViewById(R.id.finished_check_pager);
        viewPager.setAdapter(new CheckFinishedPagerAdapter(getSupportFragmentManager()));
        serviceHelper.getCheckPhotos(checkDto.getId());
    }

    private class CheckFinishedPagerAdapter extends FragmentPagerAdapter {
        public CheckFinishedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_PHOTOS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_PHOTOS.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (data != null && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_PHOTOS.name().equals(action)) {
                if(fragmentsList != null) {
                    ((CheckPhotosFragment) fragmentsList.get(1)).initGallery((ArrayList<com.lashgo.model.dto.PhotoDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.PHOTOS_LIST.name()));
                }
            }
        }
    }
}
