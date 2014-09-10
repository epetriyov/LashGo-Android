package com.lashgo.android.ui.check;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;

import javax.inject.Inject;

/**
 * Created by Eugene on 20.08.2014.
 */
public class CheckBottomPanelController implements View.OnClickListener {

    @Inject
    SettingsHelper settingsHelper;

    @Inject
    ServiceHelper serviceHelper;

    private CheckDto checkDto;

    private final BaseActivity activity;

    private TextView likesCountText;

    private int localLikesCount;

    private ImageView btnShare;

    private TextView sharesCount;

    private TextView peoplesCount;

    private ImageView btnPeoplesCount;

    private ImageView btnLikes;

    TextView commentsCount;

    ImageView btnComments;

    private PhotoDto photoDto;

    public CheckBottomPanelController(final BaseActivity activity, final PhotoDto photoDto) {
        commonInit(activity);
        this.activity = activity;
        this.photoDto = photoDto;
        if (photoDto == null) {
            throw new IllegalArgumentException("Photo info can't be empty!");
        }
        localLikesCount = photoDto.getLikesCount();
        likesCountText.setText(String.valueOf(photoDto.getLikesCount()));
        commentsCount.setText(String.valueOf(photoDto.getCommentsCount()));
        activity.findViewById(R.id.time_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.shares_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.peoples_layout).setVisibility(View.GONE);
    }

    public static enum ButtonColors {WHITE, GRAY}

    private void commonInit(final BaseActivity activity) {
        activity.inject(this);
        likesCountText = ((TextView) activity.findViewById(R.id.likes_count));
        btnLikes = (ImageView) activity.findViewById(R.id.btn_likes);
        btnLikes.setOnClickListener(this);
        commentsCount = ((TextView) activity.findViewById(R.id.comments_count));
        btnComments = (ImageView) activity.findViewById(R.id.btn_comments);
        btnComments.setOnClickListener(this);
    }

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto) {
        commonInit(activity);
        this.activity = activity;
        this.checkDto = checkDto;
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        btnShare = (ImageView) activity.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        sharesCount = ((TextView) activity.findViewById(R.id.shares_count));
        peoplesCount = ((TextView) activity.findViewById(R.id.peoples_count));
        btnPeoplesCount = (ImageView) activity.findViewById(R.id.btn_peoples_count);
        localLikesCount = checkDto.getLikesCount();
        sharesCount.setText(String.valueOf(checkDto.getSharesCount()));
        peoplesCount.setText(String.valueOf(checkDto.getPlayersCount()));
        likesCountText.setText(String.valueOf(checkDto.getLikesCount()));
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);

        if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
            activity.findViewById(R.id.likes_layout).setVisibility(View.GONE);
            activity.findViewById(R.id.comments_layout).setVisibility(View.GONE);
            final TextView checkTimeText = (TextView) activity.findViewById(R.id.check_time);
            if (checkDto.getStartDate() != null) {
                long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS;
                if (finishMillis > System.currentTimeMillis()) {
                    /**
                     * check is active
                     */
                    UiUtils.startTimer(finishMillis, checkTimeText, new TimerFinishedListener() {
                        @Override
                        public void onTimerFinished() {
                            activity.startActivity(CheckVoteActivity.buildIntent(activity,
                                    checkDto, CheckVoteActivity.class));
                            activity.finish();
                        }
                    });
                }
            }
        } else {
            activity.findViewById(R.id.time_layout).setVisibility(View.GONE);
            commentsCount.setText(String.valueOf(checkDto.getCommentsCount()));
            activity.findViewById(R.id.check_time).setVisibility(View.GONE);
        }
    }

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto, ButtonColors buttonColors) {
        this(activity, checkDto);
        updateColorsSheme(buttonColors);
    }

    private void updateColorsSheme(ButtonColors buttonColors) {
        if (buttonColors != null && ButtonColors.GRAY.name().equals(buttonColors.name())) {
            btnLikes.setImageResource(R.drawable.ic_like_gray);
            likesCountText.setTextColor(activity.getResources().getColor(R.color.vote_check_description_color));
            sharesCount.setTextColor(activity.getResources().getColor(R.color.vote_check_description_color));
            btnShare.setImageResource(R.drawable.btn_share_gray);
            commentsCount.setTextColor(activity.getResources().getColor(R.color.vote_check_description_color));
            btnComments.setImageResource(R.drawable.ic_g_comments);
            peoplesCount.setTextColor(activity.getResources().getColor(R.color.vote_check_description_color));
            btnPeoplesCount.setImageResource(R.drawable.ic_g_mob_normal);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_share) {
            //TODO not to implement
        } else if (view.getId() == R.id.btn_likes) {
            if (settingsHelper.isLoggedIn()) {
                serviceHelper.likeCheck(checkDto.getId());
            } else {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } else if (view.getId() == R.id.btn_comments) {
            //TODO implemnt comments screen
        }
    }

    public void updateLikesCount(boolean isLikeAdded) {
        if (isLikeAdded) {
            likesCountText.setText(String.valueOf(++localLikesCount));
        } else {
            likesCountText.setText(String.valueOf(--localLikesCount));
        }
    }
}

