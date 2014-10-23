package com.lashgo.android.ui.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.EditText;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.check.CheckListFragment;
import com.lashgo.android.ui.subscribes.SubscribesFragment;
import com.lashgo.android.ui.views.CustomViewPager;
import com.lashgo.android.utils.LashGoUtils;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by Eugene on 22.10.2014.
 */
public class SearchActivity extends BaseActivity {

    private EditText editSearch;
    private CustomViewPager viewPager;
    private MainStatePagerAdapter pagerAdapter;
    private TabPageIndicator tabPageIndicator;

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_search) {
            int position = viewPager.getCurrentItem();
            Fragment fragment = LashGoUtils.findFragmentByPosition(this, viewPager, pagerAdapter, position);
            if (fragment != null) {
                if (position == 0) {
                    ((CheckListFragment) fragment).setSearchText(editSearch.getText().toString());
                } else {
                    ((SubscribesFragment) fragment).setSearchText(editSearch.getText().toString());
                }
            }
        } else if (view.getId() == R.id.btn_close) {
            editSearch.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.act_search);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        editSearch = (EditText) findViewById(R.id.edit_search);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new MainStatePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.titles);
        tabPageIndicator.setViewPager(viewPager);
    }

    private class MainStatePagerAdapter extends FragmentPagerAdapter {

        private String[] content;

        public MainStatePagerAdapter(FragmentManager fm) {
            super(fm);
            content = new String[]{getString(R.string.checks), getString(R.string.peoples)};
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
                    return CheckListFragment.newInstance(CheckListFragment.StartOptions.DONT_LOAD_ON_START);
                case 1:
                    return SubscribesFragment.newInstance(-1, SubscribesFragment.ScreenType.SEARCH_USERS);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return content[position % content.length].toUpperCase();
        }
    }
}
