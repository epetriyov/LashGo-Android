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
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.model.dto.PhotoDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 18.10.2014.
 */
public class PhotoActivity extends BaseActivity {

    private ArrayList<PhotoDto> photoDtos;

    private int selectedPhotoItem;

    private String activityReferrer;

    private FragmentPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    public static Intent buildIntent(Context context, ArrayList<PhotoDto> photoDtos, int selectedPhotoItem, String activityReferrer) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.PHOTOS_LIST.name(), photoDtos);
        intent.putExtra(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
        intent.putExtra(ExtraNames.ACTIVITY_REFERRER.name(), activityReferrer);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ExtraNames.PHOTOS_LIST.name(), photoDtos);
        outState.putInt(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
        outState.putString(ExtraNames.ACTIVITY_REFERRER.name(), activityReferrer);
    }

    private void initPhotosList(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            photoDtos = (ArrayList<PhotoDto>) intent.getSerializableExtra(ExtraNames.PHOTOS_LIST.name());
            selectedPhotoItem = intent.getIntExtra(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
            activityReferrer = intent.getStringExtra(ExtraNames.ACTIVITY_REFERRER.name());
        }
        if (photoDtos == null && savedInstanceState != null) {
            photoDtos = (ArrayList<PhotoDto>) savedInstanceState.getSerializable(ExtraNames.PHOTOS_LIST.name());
            selectedPhotoItem = savedInstanceState.getInt(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
            activityReferrer = savedInstanceState.getString(ExtraNames.ACTIVITY_REFERRER.name());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initPhotosList(savedInstanceState);
        setContentView(R.layout.act_photo);
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        pagerAdapter = new PhotosPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedPhotoItem);
    }

    @Override
    protected void refresh() {
        ((BaseFragment) pagerAdapter.getItem(viewPager.getCurrentItem())).refresh();
    }

    @Override
    public void logout() {

    }

    private class PhotosPagerAdapter extends FragmentPagerAdapter {
        public PhotosPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return photoDtos.size();
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoFragment.newInstance(photoDtos.get(position), activityReferrer);
        }
    }
}
