package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.lashgo.android.R;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckFinishedActivity extends CheckBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_finished);
        ViewPager viewPager = (ViewPager) findViewById(R.id.finished_check_pager);
        viewPager.setAdapter(new CheckFinishedPagerAdapter(getSupportFragmentManager()));
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
            if (position == 1) {
                return CheckPhotosFragment.newInstance(checkDto);
            } else {
                return CheckFinishedFragment.newInstance(checkDto);
            }
        }

    }
}
