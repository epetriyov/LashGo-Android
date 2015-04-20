package com.lashgo.android.ui.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
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
    private String searchText;

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ExtraNames.SEARCH_TEXT.name(), editSearch.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_close) {
            editSearch.setText("");
        } else if (view.getId() == R.id.btn_back) {
            finish();
        }
    }

    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(ExtraNames.SEARCH_TEXT.name());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExtras(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.act_search);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new MainStatePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.titles);
        tabPageIndicator.setViewPager(viewPager);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        editSearch = (EditText) findViewById(R.id.edit_search);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    int position = viewPager.getCurrentItem();
                    Fragment fragment = LashGoUtils.findFragmentByPosition(getSupportFragmentManager(), viewPager, pagerAdapter, position);
                    if (fragment != null) {
                        if (position == 0) {
                            ((CheckListFragment) fragment).setSearchText(editSearch.getText().toString());
                        } else {
                            ((SubscribesFragment) fragment).setSearchText(editSearch.getText().toString());
                        }
                    }
                }
                return false;
            }
        });
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
                    return CheckListFragment.newInstance(CheckListFragment.StartOptions.DONT_LOAD_ON_START, searchText);
                case 1:
                    return SubscribesFragment.newInstance(-1, SubscribesFragment.ScreenType.SEARCH_USERS, searchText);
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
