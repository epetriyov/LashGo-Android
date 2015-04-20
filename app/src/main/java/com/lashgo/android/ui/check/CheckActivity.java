package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.loaders.AsyncProccessImage;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.ImageAnimation;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.dialogs.ErrorDialog;
import com.lashgo.android.ui.dialogs.MakePhotoDialog;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.PhotoDto;

import java.io.File;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckActivity extends BaseActivity implements View.OnClickListener, MakePhotoDialog.OnImageDoneListener, AsyncProccessImage.OnPhotoProcessedListener, ICheckActivity {

    private TextView win;

    public static enum TO {to, FINISHED, VOTE}

    private ImageAnimation imageAnimation;
    private String imgPath;
    private String tempImgPath;
    private CheckPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private ImageView actionBtn;
    private int checkId;
    protected CheckDto checkDto;
    private boolean isResumed;
    private boolean timerFinished;
    private TO to;
    private int pagesCount;
    private ImageView expandedImageView;
    private CheckBottomPanelController bottomPanel;
    private TextView checkName;
    private TextView checkDescription;
    private DialogFragment makePhotoFragment;
    private View voteLayout;

    @Override
    public void onPhotoProcessed(String imgPath) {
        this.imgPath = imgPath;
        this.tempImgPath = null;
        actionBtn.setImageResource(R.drawable.btn_pink_camera);
        if (pagesCount == 1) {
            pagesCount = 2;
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(1);
        } else if (pagesCount == 2) {
            ((ICheckFragment) LashGoUtils.findFragmentByPosition(getSupportFragmentManager(), viewPager, pagerAdapter, 1)).updateImage(imgPath);
        }
    }

    @Override
    public void onErrorOccured() {
        showDialog(ErrorDialog.newInstance(getString(R.string.error_load_photo)), ErrorDialog.TAG);
    }

    public static Intent buildIntent(Context context, int checkId) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(ExtraNames.CHECK_ID.name(), checkId);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ExtraNames.TEMP_IMG_PATH.name(), tempImgPath);
        outState.putString(ExtraNames.PHOTO_URL.name(), imgPath);
        outState.putInt(ExtraNames.CHECK_ID.name(), checkId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheckDto(savedInstanceState);
        setContentView(R.layout.act_check);
        initViews();
    }

    @Override
    public void startProgress() {
        showOverlayProgress();
    }

    @Override
    public void stopProgress() {
        hideOverlayProgress();
    }

    private void initCheckDto(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            tempImgPath = savedInstanceState.getString(ExtraNames.TEMP_IMG_PATH.name());
            imgPath = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
            checkId = savedInstanceState.getInt(ExtraNames.CHECK_ID.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                tempImgPath = intent.getStringExtra(ExtraNames.TEMP_IMG_PATH.name());
                imgPath = intent.getStringExtra(ExtraNames.PHOTO_URL.name());
                checkId = intent.getIntExtra(ExtraNames.CHECK_ID.name(), 0);
            }
        }
    }

    private void updatePagesCount() {
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        if ((LashgoConfig.CheckState.ACTIVE.equals(checkState) &&
                (checkDto.getUserPhotoDto() != null || !TextUtils.isEmpty(imgPath)))
                || (LashgoConfig.CheckState.FINISHED.equals(checkState) && checkDto.getWinnerPhotoDto() != null)) {
            pagesCount = 2;
        } else {
            pagesCount = 1;
        }
        pagerAdapter.notifyDataSetChanged();
        if (pagesCount == 2) {
            viewPager.setCurrentItem(1);
        }
    }

    private void initViews() {
        voteLayout = findViewById(R.id.vote_layout);
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        imageAnimation = new ImageAnimation(this, findViewById(R.id.shadow), findViewById(R.id.container), expandedImageView);
        actionBtn = (ImageView) findViewById(R.id.btn_action);
        actionBtn.setOnClickListener(this);
        checkName = ((TextView) findViewById(R.id.check_name));
        checkDescription = ((TextView) findViewById(R.id.task_description));
        win = (TextView) findViewById(R.id.win);
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        viewPager.setPageMargin(50);
        viewPager.setClipChildren(false);
        mContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhotoUtils.getScreenWidth(this) - CheckFragment.CHECH_PHOTO_PADDINGS));
        mContainer.requestLayout();
        pagerAdapter = new CheckPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        pagerAdapter.notifyDataSetChanged();
        serviceHelper.getCheck(checkId);
    }

    private void initCheckData() {
        updatePagesCount();
        bottomPanel = new CheckBottomPanelController(this, getWindow().getDecorView(), checkDto);
        checkName.setText(checkDto.getName());
        checkDescription.setText(checkDto.getDescription());
        boolean visible = checkDto.getWinnerInfo() != null && checkDto.getWinnerInfo().getId() == settingsHelper.getUserId();
        win.setVisibility(visible ? View.VISIBLE : View.GONE);
        bottomPanel.updatePeoplesCount(checkDto.getPlayersCount());
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        if (LashgoConfig.CheckState.VOTE.equals(checkState)) {
            openVotePerspective();
        } else if (LashgoConfig.CheckState.FINISHED.equals(checkState)) {
            openFinishedPerspective();
        } else {
            openActivePerspective();
        }
    }

    private void openActivePerspective() {
        voteLayout.setVisibility(View.GONE);
        if (TextUtils.isEmpty(imgPath)) {
            actionBtn.setImageResource(R.drawable.btn_camera);
        } else {
            actionBtn.setImageResource(R.drawable.btn_pink_camera);
        }
        if (checkDto.getUserPhotoDto() != null) {
            actionBtn.setVisibility(View.GONE);
        } else {
            actionBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_action) {
            if (LashGoUtils.getCheckState(checkDto).equals(LashgoConfig.CheckState.VOTE)) {
                if (settingsHelper.isLoggedIn()) {
                    startActivity(VoteProcessActivity.buildIntent(this, checkDto));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            } else {
                if (settingsHelper.isLoggedIn()) {
                    if (makePhotoFragment == null) {
                        makePhotoFragment = MakePhotoDialog.newInstance(this);
                    }
                    showDialog(makePhotoFragment, MakePhotoDialog.TAG);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        stopProgress();
        if (data != null) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                if (BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name().equals(action)) {
                    checkDto = (com.lashgo.model.dto.CheckDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.CHECK_DTO.name());
                    if (pagesCount == 2) {
                        ((ICheckFragment) LashGoUtils.findFragmentByPosition(getSupportFragmentManager(), viewPager, pagerAdapter, 1)).updateCheckDto(checkDto);
                    }
                    initCheckData();
                } else if (BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name().equals(action)) {
                    PhotoDto photoDto = new PhotoDto();
                    photoDto.setUrl(data.getString(BaseIntentHandler.ServiceExtraNames.PHOTO_PATH.name()));
                    checkDto.setUserPhotoDto(photoDto);
                    startActivity(PhotoSentActivity.buildIntent(this, new String(imgPath.toCharArray())));
                    tempImgPath = imgPath = null;
                    actionBtn.setVisibility(View.GONE);
                    ((ICheckFragment) LashGoUtils.findFragmentByPosition(getSupportFragmentManager(), viewPager, pagerAdapter, 1)).hideSendPhotoBtn();
                }
            } else {
                showDialog(ErrorDialog.newInstance(((ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA)).getErrorMessage()), ErrorDialog.TAG);
            }
        }
    }

    private void openFinishedPerspective() {
        voteLayout.setVisibility(View.GONE);
        actionBtn.setVisibility(View.GONE);
    }

    private void openVotePerspective() {
        voteLayout.setVisibility(View.VISIBLE);
        actionBtn.setVisibility(View.VISIBLE);
        actionBtn.setImageResource(R.drawable.btn_like);
        final TextView voteCheckTime = ((TextView) findViewById(R.id.vote_check_time));
        long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS + checkDto.getVoteDuration() * DateUtils.HOUR_IN_MILLIS;
        UiUtils.startTimer(finishMillis, voteCheckTime, new TimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                CheckActivity.this.onTimerFinished(TO.FINISHED);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == MakePhotoDialog.PICK_IMAGE) {
                if (data != null) {
                    tempImgPath = PhotoUtils.getPath(this, data.getData());
                    new AsyncProccessImage(tempImgPath, this).execute();
                } else {
                    ContextUtils.showToast(this, R.string.empty_image_was_chosen);
                }
            } else if (requestCode == MakePhotoDialog.CAPTURE_IMAGE) {
                new AsyncProccessImage(tempImgPath, this).execute();
            }
        }
    }

    @Override
    public void imageDone(String imagePath) {
        this.tempImgPath = imagePath;
    }

    private class CheckPagerAdapter extends FragmentPagerAdapter {

        public CheckPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return -1;
        }

        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public Fragment getItem(int position) {
            return CheckFragment.newInstance(checkDto, position, imgPath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkDto != null) {
            isResumed = true;
            if (timerFinished) {
                onTimerFinished(to);
                timerFinished = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    public void onTimerFinished(TO to) {
        this.to = to;
        if (isResumed) {
            if (to.equals(TO.VOTE)) {
                openVotePerspective();
            } else {
                serviceHelper.getCheck(checkDto.getId());
                openFinishedPerspective();
            }
        } else {
            timerFinished = true;
        }
    }

    @Override
    public void logout() {

    }

    @Override
    protected void refresh() {
        serviceHelper.getCheck(checkDto == null ? checkId : checkDto.getId());
    }

    public void loadExpandedImage(final int position, final ImageView imageView) {
        if (position == 0) {
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                PhotoUtils.displayFullImage(this, expandedImageView, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()));
            }
        } else {
            if (checkDto.getUserPhotoDto() != null && !TextUtils.isEmpty(checkDto.getUserPhotoDto().getUrl())) {
                PhotoUtils.displayFullImage(this, expandedImageView, PhotoUtils.getFullPhotoUrl(checkDto.getUserPhotoDto().getUrl()));
            } else if (!TextUtils.isEmpty(imgPath)) {
                PhotoUtils.displayFullImage(this, expandedImageView, Uri.fromFile(new File(imgPath)));
            }
        }
        imageAnimation.zoomImageFromThumb(imageView, getResources().getInteger(android.R.integer.config_shortAnimTime));
    }
}
