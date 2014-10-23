package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.model.dto.CheckDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckPhotoActivity extends BaseActivity {

    private ViewPager viewPager;

    private FragmentPagerAdapter pagerAdapter;

    private CheckDto checkDto;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckPhotoActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_photo);
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        pagerAdapter = new CheckFinishedPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
            }
        }
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
                    return PhotoFragment.newInstance(checkDto.getWinnerPhotoDto(), ActivityReferrer.FROM_CHECK_GALLERY.name());
                case 1:
                    return CheckPhotosFragment.newInstance(checkDto);
                default:
                    return null;
            }
        }
    }

    @Override
    protected void refresh() {
        PhotoFragment photoFragment = (PhotoFragment) LashGoUtils.findFragmentByPosition(this, viewPager, pagerAdapter, 0);
        if (photoFragment != null) {
            photoFragment.refresh();
        }
        CheckPhotosFragment checkPhotosFragment = (CheckPhotosFragment) LashGoUtils.findFragmentByPosition(this, viewPager, pagerAdapter, 1);
        if (checkPhotosFragment != null) {
            checkPhotosFragment.refresh();
        }
    }

    @Override
    public void logout() {

    }
}
