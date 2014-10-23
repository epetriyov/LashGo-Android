package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.ImageAnimation;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.VoteAction;
import com.lashgo.model.dto.VotePhotosResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Eugene on 20.08.2014.
 */
public class VoteProcessActivity extends BaseActivity implements View.OnClickListener, VotePhotoController.VotePhotoListener {

    private int returnedPhotosCount;
    private ScreenState screenState;
    private List<PhotoDto> votePhotos;
    private ImageView voteButton;
    private TextView voteHint;
    private TextView photosCounter;
    private VotePhotoController firstPhotoController;
    private VotePhotoController secondPhotoController;
    private VotePhotoController thirdPhotoController;
    private VotePhotoController fourthPhotoController;
    private int photosCount;
    private int votedPhotosCount = 1;
    private View baloonHint;
    private CheckDto checkDto;
    private ImageAnimation imageAnimation;
    private ImageView expandedImageView;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void clearOtherChecks(VotePhotoController votePhotoController) {
        voteHint.setText(R.string.now_this_button);
        if (firstPhotoController != votePhotoController) {
            firstPhotoController.clearCheck();
        }
        if (secondPhotoController != votePhotoController) {
            secondPhotoController.clearCheck();
        }
        if (thirdPhotoController != votePhotoController) {
            thirdPhotoController.clearCheck();
        }
        if (fourthPhotoController != votePhotoController) {
            fourthPhotoController.clearCheck();
        }
    }

    @Override
    public void openPhotoActivity(VotePhotoController votePhotoController) {
        int photoPosition = 0;
        if (votePhotoController.equals(firstPhotoController)) {
            photoPosition = 0;
        } else if (votePhotoController.equals(secondPhotoController)) {
            photoPosition = 1;
        } else if (votePhotoController.equals(thirdPhotoController)) {
            photoPosition = 2;
        } else if (votePhotoController.equals(fourthPhotoController)) {
            photoPosition = 3;
        }
        PhotoUtils.displayFullImage(this, expandedImageView, PhotoUtils.getFullPhotoUrl(votePhotos.get(photoPosition).getUrl()));
        imageAnimation.zoomImageFromThumb(votePhotoController.getImageView(), getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    @Override
    public void showOtherShadows(VotePhotoController votePhotoController) {
        if (firstPhotoController != votePhotoController) {
            firstPhotoController.showShadow();
        }
        if (secondPhotoController != votePhotoController) {
            secondPhotoController.showShadow();
        }
        if (thirdPhotoController != votePhotoController) {
            thirdPhotoController.showShadow();
        }
        if (fourthPhotoController != votePhotoController) {
            fourthPhotoController.showShadow();
        }
    }

    @Override
    public void setFirstHint() {
        voteHint.setText(R.string.choose_photo);
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCheckDto(savedInstanceState);
        setContentView(R.layout.act_first_vote);
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        imageAnimation = new ImageAnimation(this, findViewById(R.id.shadow), findViewById(R.id.container), expandedImageView);
        initViews();
        serviceHelper.getVotePhotos(checkDto.getId(), true);
        screenState = ScreenState.CHOOSE_PHOTO;
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
    }


    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name().equals(action) && data != null) {
                screenState = ScreenState.CHOOSE_PHOTO;
                voteButton.setImageResource(R.drawable.btn_like);
                voteHint.setText(R.string.choose_photo);
                firstPhotoController.newVote();
                secondPhotoController.newVote();
                thirdPhotoController.newVote();
                fourthPhotoController.newVote();
                VotePhotosResult votePhotosResult = (VotePhotosResult) data.getSerializable(BaseIntentHandler.ServiceExtraNames.VOTE_PHOTO_LIST.name());
                if (votePhotosResult != null && votePhotosResult.getVotePhotoList() != null) {
                    returnedPhotosCount = votePhotosResult.getVotePhotoList().size();
                }
                updatePhotos(votePhotosResult);
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name().equals(action)) {
                firstPhotoController.voteDone();
                secondPhotoController.voteDone();
                thirdPhotoController.voteDone();
                fourthPhotoController.voteDone();
                screenState = ScreenState.NEXT_PHOTOS;
                voteButton.setImageResource(R.drawable.btn_next_vote);
                voteHint.setText(R.string.go_to_next_partition);
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

    private void updatePhotos(VotePhotosResult votePhotos) {
        if (votePhotos != null) {
            List<PhotoDto> votePhotoList = votePhotos.getVotePhotoList();
            if (votePhotoList != null && votePhotoList.size() > 0) {
                this.votePhotos = new ArrayList<>(votePhotoList);
                if (votePhotoList.size() > 3) {
                    fourthPhotoController.setImage(this, votePhotoList.get(3).getUrl());
                }
                if (votePhotoList.size() > 2) {
                    thirdPhotoController.setImage(this, votePhotoList.get(2).getUrl());
                }
                if (votePhotoList.size() > 1) {
                    secondPhotoController.setImage(this, votePhotoList.get(1).getUrl());
                }
                if (votePhotoList.size() > 0) {
                    firstPhotoController.setImage(this, votePhotoList.get(0).getUrl());
                }
            } else {
                Toast.makeText(this, R.string.all_photos_seen, Toast.LENGTH_LONG).show();
            }
            if (votePhotos.getPhotosCount() != null) {
                photosCount = votePhotos.getPhotosCount();
            }
            updateCounter();
        }
    }

    private void initViews() {
        if (checkDto == null) {
            Toast.makeText(this, R.string.error_empty_check, Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                int taskImageSize = PhotoUtils.convertDpToPixels(48, this);
                PhotoUtils.displayImage(this, (ImageView) findViewById(R.id.task_photo), PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), taskImageSize, R.drawable.ava, false);
            }
            final TextView voteTime = (TextView) findViewById(R.id.vote_time);
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
            final View voteGallery = findViewById(R.id.vote_gallery_layout);
            voteGallery.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            int imageHeight = voteGallery.getHeight() / 2;
                            int imageWidth = (PhotoUtils.getScreenWidth(VoteProcessActivity.this) - 20) / 2;
                            firstPhotoController = new VotePhotoController(VoteProcessActivity.this, getWindow().getDecorView().getRootView(), R.id.first_vote_photo_layout, R.id.first_vote_photo, R.id.first_photo_check, R.id.first_photo_shadow, imageWidth, imageHeight);
                            secondPhotoController = new VotePhotoController(VoteProcessActivity.this, getWindow().getDecorView().getRootView(), R.id.second_vote_photo_layout, R.id.second_vote_photo, R.id.second_photo_check, R.id.second_photo_shadow, imageWidth, imageHeight);
                            thirdPhotoController = new VotePhotoController(VoteProcessActivity.this, getWindow().getDecorView().getRootView(), R.id.third_vote_photo_layout, R.id.third_vote_photo, R.id.third_photo_check, R.id.third_photo_shadow, imageWidth, imageHeight);
                            fourthPhotoController = new VotePhotoController(VoteProcessActivity.this, getWindow().getDecorView().getRootView(), R.id.fourth_vote_photo_layout, R.id.fourth_vote_photo, R.id.fourth_photo_check, R.id.fourth_photo_shadow, imageWidth, imageHeight);
                            voteGallery.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                    });
            voteButton = (ImageView) findViewById(R.id.vote_button);
            voteButton.setOnClickListener(this);
            baloonHint = findViewById(R.id.baloon_hint);
            if (settingsHelper.alreadyVoted()) {
                baloonHint.setVisibility(View.GONE);
            }
            voteHint = (TextView) findViewById(R.id.vote_hint);
            photosCounter = (TextView) findViewById(R.id.checks_counter);
            updateCounter();
            new CheckBottomPanelController(this, checkDto);

        }
    }

    private void updateCounter() {
        photosCounter.setText(String.format("%d-%d/%d", votedPhotosCount, votedPhotosCount + returnedPhotosCount - 1, photosCount));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.vote_button) {
            if (screenState.equals(ScreenState.CHOOSE_PHOTO)) {
                int selectedPhoto = 0;
                if (firstPhotoController.isChecked()) {
                    selectedPhoto = 1;
                } else if (secondPhotoController.isChecked()) {
                    selectedPhoto = 2;
                } else if (thirdPhotoController.isChecked()) {
                    selectedPhoto = 3;
                } else if (fourthPhotoController.isChecked()) {
                    selectedPhoto = 4;
                }
                if (selectedPhoto > 0) {
                    serviceHelper.votePhoto(buildVoteAction(selectedPhoto));
                }
            } else {
                votedPhotosCount += returnedPhotosCount;
                serviceHelper.getVotePhotos(checkDto.getId(), false);
                settingsHelper.firstVote();
                baloonHint.setVisibility(View.GONE);
            }
        }
    }

    private VoteAction buildVoteAction(int selectedPhoto) {
        VoteAction voteAction = new VoteAction();
        if (votePhotos != null) {
            voteAction.setVotedPhotoId(votePhotos.get(selectedPhoto - 1).getId());
            long[] photoIds = new long[votePhotos.size()];
            for (int i = 0; i < votePhotos.size(); i++) {
                photoIds[i] = votePhotos.get(i).getId();
            }
            voteAction.setPhotoIds(photoIds);
        }
        return voteAction;
    }

    private enum ScreenState {CHOOSE_PHOTO, NEXT_PHOTOS}
}
