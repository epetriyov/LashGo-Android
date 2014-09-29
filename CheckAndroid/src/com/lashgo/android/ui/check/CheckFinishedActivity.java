package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.utils.LashGoUtils;

import java.util.ArrayList;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckFinishedActivity extends CheckBaseActivity {

    private ViewPager viewPager;

    private FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_finished);
        viewPager = (ViewPager) findViewById(R.id.finished_check_pager);
        pagerAdapter = new CheckFinishedPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        serviceHelper.getCheckPhotos(checkDto.getId());
    }

    private class CheckFinishedPagerAdapter extends FragmentPagerAdapter {
        public CheckFinishedPagerAdapter(FragmentManager fm) {
            super(fm);
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
            switch (position) {
                case 0:
                    return CheckFinishedFragment.newInstance(checkDto);
                case 1:
                    return CheckPhotosFragment.newInstance(checkDto);
                default:
                    return null;
            }
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
                CheckPhotosFragment fragment = (CheckPhotosFragment) LashGoUtils.findFragmentByPosition(this, viewPager, pagerAdapter, 1);
                if (fragment != null) {
                    fragment.initGallery((ArrayList<com.lashgo.model.dto.PhotoDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.PHOTOS_LIST.name()));
                }
            }
        }
    }
}
