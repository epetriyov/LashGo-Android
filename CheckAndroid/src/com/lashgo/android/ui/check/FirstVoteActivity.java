package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.VoteAction;
import com.lashgo.model.dto.VotePhoto;
import com.lashgo.model.dto.VotePhotosResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 20.08.2014.
 */
public class FirstVoteActivity extends BaseActivity implements View.OnClickListener, VotePhotoController.VotePhotoListener {

    private static final int VOTE_PHOTOS_LIMIT = 4;
    private ScreenState screenState;
    private CheckDto checkDto;
    private List<VotePhoto> votePhotos;
    private CheckBottomPanelController bottomPanelController;
    private View voteButton;
    private TextView voteHint;
    private TextView photosCounter;
    private VotePhotoController firstPhotoController;
    private VotePhotoController secondPhotoController;
    private VotePhotoController thirdPhotoController;
    private VotePhotoController fourthPhotoController;
    private int photosCount;
    private int votedPhotosCount = 1;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckActiveActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    private void initCheckDto(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
        }
        if (checkDto == null && savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        }
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
        setContentView(R.layout.act_check_active);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initCheckDto(savedInstanceState);
        initViews();
        bottomPanelController = new CheckBottomPanelController(this, checkDto);
        serviceHelper.getVotePhotos(true);
        screenState = ScreenState.CHOOSE_PHOTO;
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_VOTE_PHOTOS.name().equals(action) && data != null) {
                screenState = ScreenState.CHOOSE_PHOTO;
                voteButton.setBackgroundResource(R.drawable.btn_like);
                voteHint.setText(R.string.choose_photo);
                updatePhotos((VotePhotosResult) data.getSerializable(BaseIntentHandler.ServiceExtraNames.VOTE_PHOTO_LIST.name()));
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name().equals(action)) {
                firstPhotoController.voteDone();
                secondPhotoController.voteDone();
                thirdPhotoController.voteDone();
                fourthPhotoController.voteDone();
                screenState = ScreenState.NEXT_PHOTOS;
                voteButton.setBackgroundResource(R.drawable.btn_next_vote);
                voteHint.setText(R.string.go_to_next_partition);
            }
        }
    }

    private void updatePhotos(VotePhotosResult votePhotos) {
        if (votePhotos != null) {
            List<VotePhoto> votePhotoList = votePhotos.getVotePhotoList();
            if (votePhotoList != null) {
                this.votePhotos = new ArrayList<>(votePhotoList);
                if (votePhotoList.size() > 0) {
                    firstPhotoController.setImage(this, votePhotoList.get(0).getPhotoUrl());
                }
                if (votePhotoList.size() > 1) {
                    secondPhotoController.setImage(this, votePhotoList.get(1).getPhotoUrl());
                }
                if (votePhotoList.size() > 2) {
                    thirdPhotoController.setImage(this, votePhotoList.get(2).getPhotoUrl());
                }
                if (votePhotoList.size() > 3) {
                    fourthPhotoController.setImage(this, votePhotoList.get(3).getPhotoUrl());
                }
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
                int taskImageSize = PhotoUtils.convertPixelsToDp(48, this);
                Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerInside().
                        resize(taskImageSize, taskImageSize).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.task_photo));
            }
            final TextView voteTime = (TextView) findViewById(R.id.vote_time);
            Calendar checkVoteCalendar = Calendar.getInstance();
            checkVoteCalendar.setTime(checkDto.getStartDate());
            checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
            new CountDownTimer(checkVoteCalendar.getTimeInMillis() - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    long remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
                    voteTime.setText(String.valueOf(remainingMinutes) + ":" + String.valueOf(remainingSeconds));
                }

                @Override
                public void onFinish() {
                    finish();
                }
            }.start();
            ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) findViewById(R.id.check_description)).setText(checkDto.getDescription());
            firstPhotoController = new VotePhotoController(this, getWindow().getDecorView().getRootView(), R.id.first_vote_photo_layout, R.id.first_vote_photo, R.id.first_photo_check, R.id.first_photo_shadow);
            secondPhotoController = new VotePhotoController(this, getWindow().getDecorView().getRootView(), R.id.second_vote_photo_layout, R.id.second_vote_photo, R.id.second_photo_check, R.id.second_photo_shadow);
            thirdPhotoController = new VotePhotoController(this, getWindow().getDecorView().getRootView(), R.id.third_vote_photo_layout, R.id.third_vote_photo, R.id.third_photo_check, R.id.third_photo_shadow);
            fourthPhotoController = new VotePhotoController(this, getWindow().getDecorView().getRootView(), R.id.fourth_vote_photo_layout, R.id.fourth_vote_photo, R.id.fourth_photo_check, R.id.fourth_photo_shadow);
            voteButton = findViewById(R.id.vote_button);
            voteButton.setOnClickListener(this);
            voteHint = (TextView) findViewById(R.id.vote_hint);
            photosCounter = (TextView) findViewById(R.id.checks_counter);
            updateCounter();
        }
    }

    private void updateCounter() {
        photosCounter.setText(String.format("%d-%d/%d", votedPhotosCount, votedPhotosCount + VOTE_PHOTOS_LIMIT, photosCount));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
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
                votedPhotosCount += VOTE_PHOTOS_LIMIT;
                serviceHelper.getVotePhotos(false);
            }
        }
    }

    private VoteAction buildVoteAction(int selectedPhoto) {
        VoteAction voteAction = new VoteAction();
        if (votePhotos != null) {
            voteAction.setVotedPhotoId(votePhotos.get(selectedPhoto - 1).getPhotoId());
            long[] photoIds = new long[votePhotos.size()];
            for (int i = 0; i < votePhotos.size(); i++) {
                photoIds[i] = votePhotos.get(i).getPhotoId();
            }
            voteAction.setPhotoIds(photoIds);
        }
        return voteAction;
    }

    private enum ScreenState {CHOOSE_PHOTO, NEXT_PHOTOS}
}
