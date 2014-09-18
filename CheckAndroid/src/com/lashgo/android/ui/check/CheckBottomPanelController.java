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
import com.lashgo.android.ui.comments.CommentsActivity;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckCounters;
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


    public void udpateCounters(CheckCounters checkCounters) {
        if (checkCounters != null) {
            localLikesCount = checkCounters.getLikesCount();
            likesCountText.setText(String.valueOf(checkCounters.getLikesCount()));
            peoplesCount.setText(String.valueOf(checkCounters.getPlayersCount()));
            commentsCount.setText(String.valueOf(checkCounters.getCommentsCount()));
        }
    }


    public CheckBottomPanelController(final BaseActivity activity, View view, final PhotoDto photoDto) {
        commonInit(activity, view);
        this.activity = activity;
        this.photoDto = photoDto;
        if (photoDto == null) {
            throw new IllegalArgumentException("Photo info can't be empty!");
        }
        view.findViewById(R.id.time_layout).setVisibility(View.GONE);
        view.findViewById(R.id.peoples_layout).setVisibility(View.GONE);
    }

    public CheckBottomPanelController(final BaseActivity activity, final PhotoDto photoDto) {
        this(activity, activity.getWindow().getDecorView(), photoDto);
    }

    public static enum ButtonColors {WHITE, GRAY}

    private void commonInit(final BaseActivity activity, final View view) {
        activity.inject(this);
        peoplesCount = ((TextView) view.findViewById(R.id.peoples_count));
        likesCountText = ((TextView) view.findViewById(R.id.likes_count));
        btnLikes = (ImageView) view.findViewById(R.id.btn_likes);
        btnLikes.setOnClickListener(this);
        commentsCount = ((TextView) view.findViewById(R.id.comments_count));
        btnComments = (ImageView) view.findViewById(R.id.btn_comments);
        btnComments.setOnClickListener(this);
    }

    public CheckBottomPanelController(final BaseActivity activity, final View view, final CheckDto checkDto) {
        commonInit(activity, view);
        this.activity = activity;
        this.checkDto = checkDto;
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        btnShare = (ImageView) view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        sharesCount = ((TextView) view.findViewById(R.id.shares_count));
        btnPeoplesCount = (ImageView) view.findViewById(R.id.btn_peoples_count);
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);

        if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
            view.findViewById(R.id.likes_layout).setVisibility(View.GONE);
            view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
            final TextView checkTimeText = (TextView) view.findViewById(R.id.check_time);
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
            view.findViewById(R.id.time_layout).setVisibility(View.GONE);
            view.findViewById(R.id.check_time).setVisibility(View.GONE);
        }
    }

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto, ButtonColors buttonColors) {
        this(activity, activity.getWindow().getDecorView(), checkDto);
        updateColorsSheme(buttonColors);
    }

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto) {
        this(activity, activity.getWindow().getDecorView(), checkDto);
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
                if (checkDto != null) {
                    serviceHelper.likeCheck(checkDto.getId());
                } else {
                    serviceHelper.likePhoto(photoDto.getId());
                }
            } else {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } else if (view.getId() == R.id.btn_comments) {
            if (checkDto != null) {
                activity.startActivity(CommentsActivity.buildCheckIntent(activity, checkDto.getId()));
            } else {
                activity.startActivity(CommentsActivity.buildPhotoIntent(activity, photoDto.getId()));
            }
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

