package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.ui.views.RobotoTextView;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckCounters;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.UserDto;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Eugene on 18.08.2014.
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener {

    private int imageSize;

    private ImageView fullImage;

    private CheckDto checkDto;

    private TextView topText;

    private ImageView taskPhoto;

    private CheckBottomPanelController bottomPanelController;

    private View bottomBg;

    private View topPhotoPanel;

    private PhotoType photoType;

    private PhotoDto photoDto;

    private String imgPath;

    public static enum PhotoType {
        TASK_PHOTO, USER_PHOTO, FROM_PROFILE_GALLERY, FROM_CHECK_GALLERY, WINNER_PHOTO
    }

    public static Intent newIntent(Context context, CheckDto checkDto, PhotoType photoType, String imgPath) {
        Intent intent = newIntent(context, checkDto, photoType);
        intent.putExtra(ExtraNames.PHOTO_URL.name(), imgPath);
        return intent;
    }

    public static Intent newIntent(Context context, CheckDto checkDto, PhotoType photoType) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        intent.putExtra(ExtraNames.PHOTO_TYPE.name(), photoType);
        return intent;
    }

    public static Intent newIntent(Context context, PhotoDto photoDto, PhotoType photoType) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.PHOTO_DTO.name(), photoDto);
        intent.putExtra(ExtraNames.PHOTO_TYPE.name(), photoType);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.PHOTO_TYPE.name(), photoType);
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        outState.putSerializable(ExtraNames.PHOTO_DTO.name(), photoDto);
        outState.putString(ExtraNames.PHOTO_URL.name(), imgPath);
        super.onSaveInstanceState(outState);
    }

    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            imgPath = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
            photoType = (PhotoType) savedInstanceState.getSerializable(ExtraNames.PHOTO_TYPE.name());
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
            photoDto = (PhotoDto) savedInstanceState.getSerializable(ExtraNames.PHOTO_DTO.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                imgPath = intent.getStringExtra(ExtraNames.PHOTO_URL.name());
                photoType = (PhotoType) intent.getSerializableExtra(ExtraNames.PHOTO_TYPE.name());
                checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
                photoDto = (PhotoDto) intent.getSerializableExtra(ExtraNames.PHOTO_DTO.name());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP);
        initExtras(savedInstanceState);
        setContentView(R.layout.adt_photo);
        initViews();
        if (checkDto != null) {
            if (PhotoType.WINNER_PHOTO.name().equals(photoType.name())) {
                bottomPanelController = new CheckBottomPanelController(CheckBottomPanelController.FROM.PHOTO, this, checkDto.getWinnerPhotoDto());
            } else {
                bottomPanelController = new CheckBottomPanelController(CheckBottomPanelController.FROM.PHOTO, this, checkDto);
            }
        } else {
            bottomPanelController = new CheckBottomPanelController(CheckBottomPanelController.FROM.PHOTO, this, photoDto);
        }
        initCounters();
    }

    private void initCounters() {
        CheckCounters checkCounters = new CheckCounters();
        if (photoDto != null) {
            checkCounters.setLikesCount(photoDto.getLikesCount());
            checkCounters.setCommentsCount(photoDto.getCommentsCount());
        } else if (checkDto.getWinnerPhotoDto() != null) {
            checkCounters.setLikesCount(checkDto.getWinnerPhotoDto().getLikesCount());
            checkCounters.setCommentsCount(checkDto.getWinnerPhotoDto().getCommentsCount());
        }
        bottomPanelController.udpateCounters(checkCounters);
    }

    private void setUpTopCheck(CheckDto checkDto) {
        if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            PhotoUtils.displayImage(this, taskPhoto, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
        }
        topText.setText(checkDto.getName());
    }

    private void initViews() {
        fullImage = ((ImageView) findViewById(R.id.full_photo));
        fullImage.setOnClickListener(this);
        topPhotoPanel = findViewById(R.id.top_check_panel);
        bottomBg = findViewById(R.id.bottom_photo_bg);
        imageSize = PhotoUtils.convertDpToPixels(40, this);
        topText = ((RobotoTextView) findViewById(R.id.check_name));
        topText.setOnClickListener(this);
        taskPhoto = (ImageView) findViewById(R.id.task_photo);
        taskPhoto.setOnClickListener(this);
        if (checkDto != null) {
            if (!TextUtils.isEmpty(imgPath)) {
                Picasso.with(this).load(Uri.fromFile(new File(imgPath))).fit().centerInside().into(fullImage);
                setUpTopCheck(checkDto);
            } else if (photoType != null) {
                if (PhotoType.TASK_PHOTO.name().equals(photoType.name()) && !TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                    Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).fit().centerInside().into(fullImage);
                    setUpTopCheck(checkDto);
                } else if (PhotoType.USER_PHOTO.name().equals(photoType.name()) && checkDto.getUserPhotoDto() != null && !TextUtils.isEmpty(checkDto.getUserPhotoDto().getUrl())) {
                    Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getUserPhotoDto().getUrl())).fit().centerInside().into(fullImage);
                    setUpTopCheck(checkDto);
                } else if (PhotoType.WINNER_PHOTO.name().equals(photoType.name()) && checkDto.getWinnerPhotoDto() != null && !TextUtils.isEmpty(checkDto.getWinnerPhotoDto().getUrl())) {
                    Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoDto().getUrl())).fit().centerInside().into(fullImage);
                    setUpTopUser(checkDto.getWinnerInfo());
                    findViewById(R.id.medal).setVisibility(View.VISIBLE);
                }
            }
        } else if (photoDto != null && !TextUtils.isEmpty(photoDto.getUrl())) {
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(photoDto.getUrl())).fit().centerInside().into(fullImage);
            if (PhotoType.FROM_PROFILE_GALLERY.name().equals(photoType.name())) {
                setUpTopCheck(photoDto.getCheck());
            } else {
                setUpTopUser(photoDto.getUser());
            }
        }
    }

    private void setUpTopUser(UserDto userDto) {
        if (userDto != null) {
            if (!TextUtils.isEmpty(userDto.getAvatar())) {
                PhotoUtils.displayImage(this, taskPhoto, LashGoUtils.getUserAvatarUrl(userDto.getAvatar()), imageSize, R.drawable.ava, false);
            }
            topText.setText(TextUtils.isEmpty(userDto.getFio()) ? userDto.getLogin() : userDto.getFio());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.full_photo) {
            if (topPhotoPanel.getVisibility() == View.VISIBLE) {
                topPhotoPanel.setVisibility(View.GONE);
                bottomBg.setVisibility(View.GONE);
            } else {
                topPhotoPanel.setVisibility(View.VISIBLE);
                bottomBg.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.task_photo || view.getId() == R.id.check_name) {
            if (PhotoType.WINNER_PHOTO.name().equals(photoType.name()) && checkDto != null && checkDto.getWinnerInfo() != null) {
                startActivity(ProfileActivity.buildIntent(this, checkDto.getWinnerInfo().getId()));
            } else if (PhotoType.FROM_CHECK_GALLERY.name().equals(photoType.name()) && photoDto != null && photoDto.getUser() != null) {
                startActivity(ProfileActivity.buildIntent(this, photoDto.getUser().getId()));
            } else if (photoDto != null && photoDto.getCheck() != null) {
                serviceHelper.getCheck(photoDto.getCheck().getId());
            }
        }
    }


    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (data != null && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name().equals(action)) {
                Boolean isLikeAdded = data.getBoolean(BaseIntentHandler.ServiceExtraNames.IS_LIKE_ADDED.name());
                if (isLikeAdded != null) {
                    bottomPanelController.updateLikesCount(isLikeAdded.booleanValue());
                }
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK.name().equals(action)) {
                CheckDto selectedCheck = (CheckDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.CHECK_DTO.name());
                if (selectedCheck != null) {
                    LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(selectedCheck);
                    switch (checkState) {
                        case ACTIVE:
                            startActivity(CheckActiveActivity.buildIntent(this, selectedCheck, CheckActiveActivity.class));
                            break;
                        case VOTE:
                            startActivity(CheckVoteActivity.buildIntent(this, selectedCheck, CheckVoteActivity.class));
                            break;
                        case FINISHED:
                            startActivity(CheckFinishedActivity.buildIntent(this, selectedCheck, CheckFinishedActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
