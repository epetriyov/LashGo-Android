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

import javax.inject.Inject;

/**
 * Created by Eugene on 20.08.2014.
 */
public class CheckBottomPanelController implements View.OnClickListener {

    @Inject
    SettingsHelper settingsHelper;

    @Inject
    ServiceHelper serviceHelper;

    private final CheckDto checkDto;

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

    public static enum ButtonColors {WHITE, GRAY}

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto) {
        activity.inject(this);
        this.activity = activity;
        this.checkDto = checkDto;
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        localLikesCount = checkDto.getLikesCount();
        btnShare = (ImageView) activity.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        sharesCount = ((TextView) activity.findViewById(R.id.shares_count));
        sharesCount.setText(String.valueOf(checkDto.getSharesCount()));
        peoplesCount = ((TextView) activity.findViewById(R.id.peoples_count));
        peoplesCount.setText(String.valueOf(checkDto.getPlayersCount()));
        btnPeoplesCount = (ImageView) activity.findViewById(R.id.btn_peoples_count);
        likesCountText = ((TextView) activity.findViewById(R.id.likes_count));
        likesCountText.setText(String.valueOf(checkDto.getLikesCount()));
        btnLikes = (ImageView) activity.findViewById(R.id.btn_likes);
        btnLikes.setOnClickListener(this);
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
            commentsCount = ((TextView) activity.findViewById(R.id.comments_count));
            commentsCount.setText(String.valueOf(checkDto.getCommentsCount()));
            btnComments = (ImageView) activity.findViewById(R.id.btn_comments);
            btnComments.setOnClickListener(this);
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
            likesCountText.setTextColor(R.color.vote_check_description_color);
            sharesCount.setTextColor(R.color.vote_check_description_color);
            btnShare.setImageResource(R.drawable.btn_share_gray);
            commentsCount.setTextColor(R.color.vote_check_description_color);
            btnComments.setImageResource(R.drawable.ic_g_comments);
            peoplesCount.setTextColor(R.color.vote_check_description_color);
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
