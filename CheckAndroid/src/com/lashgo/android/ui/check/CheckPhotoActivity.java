package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckPhotoActivity extends BaseActivity {

    private ViewPager viewPager;

    private FragmentPagerAdapter pagerAdapter;

    private CheckDto checkDto;

    private View rigthArrow;

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
        rigthArrow = findViewById(R.id.right_arrow);
        rigthArrow.setVisibility(View.VISIBLE);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    rigthArrow.setVisibility(View.VISIBLE);
                } else {
                    rigthArrow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
