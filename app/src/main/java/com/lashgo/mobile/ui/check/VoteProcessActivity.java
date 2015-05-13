package com.lashgo.mobile.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.mobile.LashgoConfig;
import com.lashgo.mobile.R;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.ui.views.GradientImageView;
import com.lashgo.mobile.ui.views.PagerContainer;
import com.lashgo.mobile.utils.ContextUtils;
import com.lashgo.mobile.utils.PhotoUtils;
import com.lashgo.mobile.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.VotePhoto;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Eugene on 20.08.2014.
 */
public class VoteProcessActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<VotePhoto> votePhotos;
    private CheckDto checkDto;
    private FragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private View leftArrow;
    private View rightArrow;
    private int pagesCount = 0;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, VoteProcessActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheckDto(savedInstanceState);
        setContentView(R.layout.act_first_vote);
        if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            int taskImageSize = PhotoUtils.convertDpToPixels(40, this);
            PhotoUtils.displayImage(this, (ImageView) findViewById(R.id.task_photo), PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), taskImageSize, R.drawable.ava, false);
        }
        final TextView voteTime = (TextView) findViewById(R.id.vote_time);
        GradientImageView checkGradient = (GradientImageView) findViewById(R.id.check_gradient);
        checkGradient.updateImage(LashgoConfig.CheckState.VOTE, 1f);
        Calendar checkVoteCalendar = Calendar.getInstance();
        checkVoteCalendar.setTime(checkDto.getStartDate());
        checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
        UiUtils.startTimer(checkVoteCalendar.getTimeInMillis(), voteTime, new TimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                finish();
            }
        });
        ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
        ((TextView) findViewById(R.id.check_description)).setText(checkDto.getDescription());
        leftArrow = findViewById(R.id.left_arrow);
        rightArrow = findViewById(R.id.right_arrow);
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        pagerAdapter = new VotesPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
        new CheckBottomPanelController(this, checkDto);
        serviceHelper.getVotePhotos(checkDto.getId());
    }

    private void initCheckDto(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
            }
        }
        if (checkDto == null) {
            ContextUtils.showToast(this, R.string.error_empty_check);
            finish();
        }
    }


    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name().equals(action) && data != null) {
                votePhotos = (ArrayList<VotePhoto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.VOTE_PHOTO_LIST.name());
                calcPagesCount();
                pagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(pagesCount - 1);
            }
        }
    }

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {
        settingsHelper.logout();
        finish();
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    private void updateArrows(int currentPosition) {
        if (pagerAdapter.getCount() < 2) {
            leftArrow.setVisibility(View.GONE);
            rightArrow.setVisibility(View.GONE);
        } else if (currentPosition == 0 && pagerAdapter.getCount() > 1) {
            leftArrow.setVisibility(View.GONE);
            rightArrow.setVisibility(View.VISIBLE);
        } else if (currentPosition == pagerAdapter.getCount() - 1) {
            leftArrow.setVisibility(View.VISIBLE);
            rightArrow.setVisibility(View.GONE);
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

    public void continueVoting() {
//        int votePhotosCount = pagerAdapter.getCount() * 4 + 4 < votePhotos.size() ? pagerAdapter.getCount() * 4 + 4 : votePhotos.size();
//        pagesCount = votePhotosCount > 0 ? (votePhotosCount % 4 != 0 ? votePhotosCount / 4 + 1 : votePhotosCount / 4) : 0;
//        pagerAdapter.notifyDataSetChanged();
//        viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
        serviceHelper.getVotePhotos(checkDto.getId());
    }

    private class VotesPagerAdapter extends FragmentPagerAdapter {
        public VotesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public Fragment getItem(int position) {
            ArrayList<VotePhoto> votePhotoList = new ArrayList<>(position * 4 + 4 < votePhotos.size() ? votePhotos.subList(position * 4, position * 4 + 4) : votePhotos.subList(position * 4, votePhotos.size()));
            return VoteFragment.newInstance(checkDto.getId(), votePhotoList, position, votePhotos.size());
        }
    }

    private void calcPagesCount() {
        int votePhotosCount = 0;
        if (votePhotos != null && votePhotos.size() > 0) {
            int i = 0;
            while (i < votePhotos.size() && votePhotos.get(i).isShown()) {
                i++;
            }
            votePhotosCount = i + 4 < votePhotos.size() ? i + 4 : votePhotos.size();
        }
        pagesCount = votePhotosCount > 0 ? (votePhotosCount % 4 != 0 ? votePhotosCount / 4 + 1 : votePhotosCount / 4) : 0;
    }
}
