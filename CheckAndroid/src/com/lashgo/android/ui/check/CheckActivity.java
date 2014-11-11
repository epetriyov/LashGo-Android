package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
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

import java.io.File;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, MakePhotoDialog.OnImageDoneListener {

    private static final int CHECH_PHOTO_PADDINGS = 130;
    private static final String TASK_PHOTO_TAG = "task_photo";
    private static final String USER_PHOTO_TAG = "user_photo";
    private static final String MADE_PHOTO_TAG = "made_photo";
    private static final String WINNER_PHOTO_TAG = "winner_photo";
    private String imgPath;
    private CheckPagerAdapter pagerAdapter;
    private int imageSize;
    private ViewPager viewPager;
    private ImageView actionBtn;
    private boolean wasSent;
    private int checkId;
    protected CheckDto checkDto;
    private boolean isResumed;
    private boolean timerFinished;
    private TO to;
    private int pagesCount;
    private ImageAnimation imageAnimation;
    private ImageView expandedImageView;
    private CheckBottomPanelController bottomPanel;
    private TextView checkName;
    private TextView checkDescription;
    private DialogFragment makePhotoFragment;

    public enum TO {to, FINISHED, VOTE}

    public static Intent buildIntent(Context context, int checkId) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(ExtraNames.CHECK_ID.name(), checkId);
        return intent;
    }

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        outState.putString(ExtraNames.PHOTO_URL.name(), imgPath);
        outState.putBoolean(ExtraNames.WAS_PHOTO_SENT.name(), wasSent);
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
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheckDto(savedInstanceState);
        setContentView(R.layout.act_check);
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        imageAnimation = new ImageAnimation(this, findViewById(R.id.shadow), findViewById(R.id.container), expandedImageView);
        initViews();
        if (checkDto == null) {
            serviceHelper.getCheck(checkId);
        } else {
            bottomPanel = new CheckBottomPanelController(this, getWindow().getDecorView(), checkDto);
            updateCheckInfo();
            loadCheck();
        }
    }

    @Override
    public void startProgress() {
        showOverlayProgress();
    }

    @Override
    public void stopProgress() {
        hideOverlayProgress();
    }

    private void updateCheckInfo() {
        checkName.setText(checkDto.getName());
        checkDescription.setText(checkDto.getDescription());
        updatePagesCount();
    }

    private void initCheckDto(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
            imgPath = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
            wasSent = savedInstanceState.getBoolean(ExtraNames.WAS_PHOTO_SENT.name(), false);
            checkId = savedInstanceState.getInt(ExtraNames.CHECK_ID.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
                imgPath = intent.getStringExtra(ExtraNames.PHOTO_URL.name());
                wasSent = intent.getBooleanExtra(ExtraNames.WAS_PHOTO_SENT.name(), false);
                checkId = intent.getIntExtra(ExtraNames.CHECK_ID.name(), 0);
            }
        }
    }

    private void updatePagesCount() {
        if (checkDto == null) {
            pagesCount = 0;
        } else {
            LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
            if ((LashgoConfig.CheckState.ACTIVE.equals(checkState) &&
                    (checkDto.getUserPhotoDto() != null || !TextUtils.isEmpty(imgPath)))
                    || (LashgoConfig.CheckState.FINISHED.equals(checkState) && checkDto.getWinnerPhotoDto() != null)) {
                pagesCount = 2;
            } else {
                pagesCount = 1;
            }
        }
    }

    private void initViews() {
        imageSize = PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS;
        actionBtn = (ImageView) findViewById(R.id.btn_action);
        actionBtn.setOnClickListener(this);
        checkName = ((TextView) findViewById(R.id.check_name));
        checkDescription = ((TextView) findViewById(R.id.task_description));
        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
        viewPager = mContainer.getViewPager();
        viewPager.setPageMargin(50);
        viewPager.setClipChildren(false);
        mContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageSize));
        mContainer.requestLayout();
        mContainer.setOnPageChangeListener(this);
        updatePagesCount();
        pagerAdapter = new CheckPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        if (pagerAdapter.getCount() > 1) {
            viewPager.setCurrentItem(1);
        }
    }

    private void loadCheck() {
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
        findViewById(R.id.vote_layout).setVisibility(View.GONE);
        actionBtn.setImageResource(R.drawable.btn_camera);
        if (checkDto.getUserPhotoDto() != null || wasSent) {
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
                startActivity(VoteProcessActivity.buildIntent(this, checkDto));
            } else {
                if (settingsHelper.isLoggedIn()) {
                    if (makePhotoFragment == null) {
                        makePhotoFragment = new MakePhotoDialog(this);
                    }
                    showDialog(makePhotoFragment, MakePhotoDialog.TAG);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        } else if (view.getId() == R.id.btn_send_photo) {
            if (!TextUtils.isEmpty(imgPath) && new File(imgPath).exists()) {
                serviceHelper.sendPhoto(checkDto.getId(), imgPath);
            } else {
                ContextUtils.showToast(this, R.string.error_send_photo);
            }
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        stopProgress();
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name().equals(action)) {
                startActivity(PhotoSentActivity.buildIntent(this, new String(imgPath)));
                wasSent = true;
                actionBtn.setVisibility(View.GONE);
                pagerAdapter.hideSendPhotoBtn();
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name().equals(action)) {
                if (data != null) {
                    checkDto = (com.lashgo.model.dto.CheckDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.CHECK_DTO.name());
                    if (bottomPanel == null) {
                        bottomPanel = new CheckBottomPanelController(this, getWindow().getDecorView(), checkDto);
                    }
                    updateCheckInfo();
                    loadCheck();
                    pagerAdapter.notifyDataSetChanged();
                }
            }
        } else {
            showDialog(ErrorDialog.newInstance(data != null ? (ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA) : null), ErrorDialog.TAG);
        }
    }

    private void openFinishedPerspective() {
        findViewById(R.id.vote_layout).setVisibility(View.GONE);
        actionBtn.setVisibility(View.GONE);
    }

    private void openVotePerspective() {
        findViewById(R.id.vote_layout).setVisibility(View.VISIBLE);
        actionBtn.setImageResource(R.drawable.btn_like);
        final TextView voteCheckTime = ((TextView) findViewById(R.id.vote_check_time));
        long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS + checkDto.getVoteDuration() * DateUtils.HOUR_IN_MILLIS;
        UiUtils.startTimer(finishMillis, voteCheckTime, new TimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                CheckActivity.this.onTimerFinished(TO.FINISHED);
            }
        });
        updatePagesCount();
        pagerAdapter.notifyDataSetChanged();
    }

    private class AsyncProccessImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return PhotoUtils.compressImage(imgPath);
        }

        @Override
        protected void onPostExecute(String fileName) {
            imgPath = fileName;
            addMinePhoto();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == MakePhotoDialog.PICK_IMAGE) {
                if (data != null) {
                    imgPath = PhotoUtils.getPath(this, data.getData());
                    new AsyncProccessImage().execute();
                } else {
                    ContextUtils.showToast(this, R.string.empty_image_was_chosen);
                }
            } else if (requestCode == MakePhotoDialog.CAPTURE_IMAGE) {
                new AsyncProccessImage().execute();
            }
        } else {
            imgPath = null;
        }
    }

    /**
     * check photo done
     */
    private void addMinePhoto() {
        actionBtn.setImageResource(R.drawable.btn_pink_camera);
        pagerAdapter.photoAdded();
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1 && imgPath != null && !wasSent) {
            pagerAdapter.showSendPhotoBtn();
        } else {
            pagerAdapter.hideSendPhotoBtn();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void imageDone(String imagePath) {
        this.imgPath = imagePath;
    }

    private class CheckPagerAdapter extends PagerAdapter {

        private View btnSend;

        public void photoAdded() {
            pagesCount = 2;
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            View view = getLayoutInflater().inflate(R.layout.adt_check_pager, null);
            final ImageView checkImage = (ImageView) view.findViewById(R.id.check_photo);
            checkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 1 && checkDto.getWinnerPhotoDto() != null) {
                        checkDto.getWinnerPhotoDto().setUser(checkDto.getWinnerInfo());
                        startActivity(CheckPhotoActivity.buildIntent(CheckActivity.this, checkDto));
                    } else {
                        loadExpandedImage();
                        imageAnimation.zoomImageFromThumb(checkImage, getResources().getInteger(android.R.integer.config_shortAnimTime));
                    }
                }
            });
            View btnSend = view.findViewById(R.id.btn_send_photo);
            btnSend.setOnClickListener(CheckActivity.this);
            View winnerMedal = view.findViewById(R.id.winner_medal);
            TextView winnerName = (TextView) view.findViewById(R.id.winner_name);
            container.addView(view);
            LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
            switch (position) {
                case 0:
                    checkImage.setTag(TASK_PHOTO_TAG);
                    btnSend.setVisibility(View.GONE);
                    winnerMedal.setVisibility(View.GONE);
                    winnerName.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                        PhotoUtils.displayImage(CheckActivity.this, checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
                    }
                    break;
                case 1:
                    if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
                        this.btnSend = btnSend;
                        if (imgPath != null && !wasSent) {
                            btnSend.setVisibility(View.VISIBLE);
                        } else {
                            btnSend.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(imgPath)) {
                            checkImage.setTag(MADE_PHOTO_TAG);
                            PhotoUtils.displayImage(CheckActivity.this, checkImage, Uri.fromFile(new File(imgPath)), imageSize, R.drawable.ava, false);
                        } else {
                            if (checkDto.getUserPhotoDto() != null && !TextUtils.isEmpty(checkDto.getUserPhotoDto().getUrl())) {
                                checkImage.setTag(USER_PHOTO_TAG);
                                PhotoUtils.displayImage(CheckActivity.this, checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getUserPhotoDto().getUrl()), imageSize, R.drawable.ava, false);
                            }
                        }
                        winnerMedal.setVisibility(View.GONE);
                        winnerName.setVisibility(View.GONE);
                    } else if (LashgoConfig.CheckState.FINISHED.equals(checkState)) {
                        winnerMedal.setVisibility(View.VISIBLE);
                        winnerName.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                        winnerName.setText(TextUtils.isEmpty(checkDto.getWinnerInfo().getFio()) ? checkDto.getWinnerInfo().getLogin() : checkDto.getWinnerInfo().getFio());
                        if (checkDto.getWinnerPhotoDto() != null && !TextUtils.isEmpty(checkDto.getWinnerPhotoDto().getUrl())) {
                            checkImage.setTag(WINNER_PHOTO_TAG);
                            PhotoUtils.displayImage(CheckActivity.this, checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoDto().getUrl()), imageSize, R.drawable.ava, true);
                        }
                    }
                    break;
                default:
                    break;
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        public void showSendPhotoBtn() {
            if (btnSend != null) {
                btnSend.setVisibility(View.VISIBLE);
            }
        }

        public void hideSendPhotoBtn() {
            if (btnSend != null) {
                btnSend.setVisibility(View.GONE);
            }
        }
    }

    private void loadExpandedImage() {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                PhotoUtils.displayFullImage(CheckActivity.this, expandedImageView, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()));
            }
        } else {
            if (!TextUtils.isEmpty(imgPath)) {
                PhotoUtils.displayFullImage(CheckActivity.this, expandedImageView, Uri.fromFile(new File(imgPath)));
            } else if (checkDto.getUserPhotoDto() != null && !TextUtils.isEmpty(checkDto.getUserPhotoDto().getUrl())) {
                PhotoUtils.displayFullImage(CheckActivity.this, expandedImageView, PhotoUtils.getFullPhotoUrl(checkDto.getUserPhotoDto().getUrl()));
            }
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
            finish();
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
}
