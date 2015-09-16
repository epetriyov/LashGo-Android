package com.lashgo.mobile.ui.photo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lashgo.mobile.R;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.ui.BaseFragment;
import com.lashgo.mobile.ui.check.ActivityReferrer;
import com.lashgo.mobile.ui.dialogs.ErrorDialog;
import com.lashgo.mobile.ui.views.PagerContainer;
import com.lashgo.mobile.utils.ContextUtils;
import com.lashgo.mobile.utils.LashGoUtils;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.PhotoDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 18.10.2014.
 */
public class PhotoActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<PhotoDto> photoDtos;

    private int selectedPhotoItem;

    private String activityReferrer;

    private FragmentPagerAdapter pagerAdapter;

    private ViewPager viewPager;
    private View leftArrow;
    private View rightArrow;
    private long photoId;

    public static Intent buildIntent(Context context, long photoId) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.PHOTO_ID.name(), photoId);
        return intent;
    }

    public static Intent buildVoteIntent(Context context, long photoId) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.PHOTO_ID.name(), photoId);
        intent.putExtra(ExtraNames.ACTIVITY_REFERRER.name(), ActivityReferrer.FROM_CHECK_GALLERY.name());
        return intent;
    }

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
        outState.putLong(ExtraNames.PHOTO_ID.name(), photoId);
    }

    private void initPhotosList(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            photoDtos = (ArrayList<PhotoDto>) savedInstanceState.getSerializable(ExtraNames.PHOTOS_LIST.name());
            selectedPhotoItem = savedInstanceState.getInt(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
            activityReferrer = savedInstanceState.getString(ExtraNames.ACTIVITY_REFERRER.name());
            photoId = savedInstanceState.getLong(ExtraNames.PHOTO_ID.name());
        } else if (intent != null) {
            photoDtos = (ArrayList<PhotoDto>) intent.getSerializableExtra(ExtraNames.PHOTOS_LIST.name());
            selectedPhotoItem = intent.getIntExtra(ExtraNames.SELECTED_PHOTO.name(), selectedPhotoItem);
            activityReferrer = intent.getStringExtra(ExtraNames.ACTIVITY_REFERRER.name());
            photoId = intent.getLongExtra(ExtraNames.PHOTO_ID.name(), -1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_notifications);
        menuItem.setVisible(true);
        ImageView notificationsView = (ImageView) menuItem.getActionView();
        notificationsView.setImageResource(R.drawable.banned_icon);
        notificationsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceHelper.complainPhoto(photoDtos.get(viewPager.getCurrentItem()).getId());
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initPhotosList(savedInstanceState);
        setContentView(R.layout.act_photo);
        leftArrow = findViewById(R.id.left_arrow);
        rightArrow = findViewById(R.id.right_arrow);
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        pagerAdapter = new PhotosPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedPhotoItem);
        updateArrows(selectedPhotoItem);
        viewPager.setOnPageChangeListener(this);
        if (photoId > 0) {
            serviceHelper.getPhoto(photoId);
        }
    }

    @Override
    protected void refresh() {
        Fragment fragment = LashGoUtils.findFragmentByPosition(getSupportFragmentManager(), viewPager, pagerAdapter, viewPager.getCurrentItem());
        if (fragment != null) {
            ((BaseFragment) fragment).refresh();
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        stopProgress();
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO.name().equals(action) && data != null) {
                photoDtos = new ArrayList<>(1);
                photoDtos.add((PhotoDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.PHOTO_DTO.name()));
                pagerAdapter.notifyDataSetChanged();
            } else {
                ContextUtils.showToast(this, R.string.complain_sent);
            }
        } else {
            if (data != null) {
                showDialog(ErrorDialog.newInstance(((ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA)).getErrorMessage()), ErrorDialog.TAG);
            }
        }
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_COMPLAIN_PHOTO.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_COMPLAIN_PHOTO.name());
    }

    @Override
    public void logout() {

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    private void updateArrows(int currentPosition) {
        if (photoDtos == null || photoDtos.size() < 2) {
            rightArrow.setVisibility(View.GONE);
            leftArrow.setVisibility(View.GONE);
        } else if (currentPosition == 0) {
            rightArrow.setVisibility(View.VISIBLE);
            leftArrow.setVisibility(View.GONE);
        } else if (currentPosition == photoDtos.size() - 1) {
            rightArrow.setVisibility(View.GONE);
            leftArrow.setVisibility(View.VISIBLE);
        } else {
            rightArrow.setVisibility(View.VISIBLE);
            leftArrow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageSelected(int i) {
        updateArrows(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
            return photoDtos != null ? photoDtos.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoFragment.newInstance(photoDtos.get(position), activityReferrer);
        }
    }
}
