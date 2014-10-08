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
import java.lang.ref.WeakReference;

/**
 * Created by Eugene on 20.08.2014.
 */
public class CheckBottomPanelController implements View.OnClickListener {

    private FROM from;

    public static enum FROM {PHOTO, from, CHECK}

    @Inject
    SettingsHelper settingsHelper;

    @Inject
    ServiceHelper serviceHelper;

    private CheckDto checkDto;

    private final WeakReference<BaseActivity> activity;

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


    public CheckBottomPanelController(final FROM from, final BaseActivity activity, View view, final PhotoDto photoDto) {
        commonInit(from, activity, view);
        this.activity = new WeakReference<BaseActivity>(activity);
        this.photoDto = photoDto;
        if (photoDto == null) {
            throw new IllegalArgumentException("Photo info can't be empty!");
        }
        view.findViewById(R.id.time_layout).setVisibility(View.GONE);
        view.findViewById(R.id.peoples_layout).setVisibility(View.GONE);
    }

    public CheckBottomPanelController(final FROM from, final BaseActivity activity, final PhotoDto photoDto) {
        this(from, activity, activity.getWindow().getDecorView(), photoDto);
    }

    public static enum ButtonColors {WHITE, GRAY}

    private void commonInit(final FROM from, final BaseActivity activity, final View view) {
        activity.inject(this);
        this.from = from;
        peoplesCount = ((TextView) view.findViewById(R.id.peoples_count));
        likesCountText = ((TextView) view.findViewById(R.id.likes_count));
        btnLikes = (ImageView) view.findViewById(R.id.btn_likes);
        btnLikes.setOnClickListener(this);
        commentsCount = ((TextView) view.findViewById(R.id.comments_count));
        btnComments = (ImageView) view.findViewById(R.id.btn_comments);
        btnComments.setOnClickListener(this);
    }

    public CheckBottomPanelController(final FROM from, final BaseActivity activity, final View view, final CheckDto checkDto) {
        commonInit(from, activity, view);
        this.activity = new WeakReference<BaseActivity>(activity);
        this.checkDto = checkDto;
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        btnShare = (ImageView) view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        sharesCount = ((TextView) view.findViewById(R.id.shares_count));
        btnPeoplesCount = (ImageView) view.findViewById(R.id.btn_peoples_count);
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        if (from.equals(FROM.CHECK)) {
            /**
             * we are at check screen
             */
            if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
                /**
                 * check is active
                 */
                view.findViewById(R.id.likes_layout).setVisibility(View.GONE);
                view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
                final TextView checkTimeText = (TextView) view.findViewById(R.id.check_time);
                if (checkDto.getStartDate() != null) {
                    long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS;
                    if (finishMillis > System.currentTimeMillis()) {
                        UiUtils.startTimer(finishMillis, checkTimeText, new TimerFinishedListener() {
                            @Override
                            public void onTimerFinished() {
                                if (CheckBottomPanelController.this.activity.get() != null && CheckBottomPanelController.this.activity.get() instanceof CheckBaseActivity) {
                                    ((CheckBaseActivity) CheckBottomPanelController.this.activity.get()).onTimerFinished(CheckBaseActivity.TO.VOTE);
                                }

                            }
                        });
                    }
                }
            } else if (LashgoConfig.CheckState.VOTE.equals(checkState)) {
                /**
                 * vote is going
                 */
                view.findViewById(R.id.likes_layout).setVisibility(View.GONE);
                view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
                view.findViewById(R.id.time_layout).setVisibility(View.GONE);
                view.findViewById(R.id.check_time).setVisibility(View.GONE);
            } else {
                /**
                 * check is finished
                 */
                if (checkDto.getWinnerPhotoDto() == null) {
                    view.findViewById(R.id.likes_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
                }
                view.findViewById(R.id.time_layout).setVisibility(View.GONE);
                view.findViewById(R.id.check_time).setVisibility(View.GONE);
            }
        } else {
            /**
             * we are at the photo's screen
             */
            if (photoDto == null) {
                view.findViewById(R.id.likes_layout).setVisibility(View.GONE);
                view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
            }
            view.findViewById(R.id.time_layout).setVisibility(View.GONE);
            view.findViewById(R.id.check_time).setVisibility(View.GONE);
        }
    }

    public CheckBottomPanelController(final FROM from, final BaseActivity activity, final CheckDto checkDto, ButtonColors buttonColors) {
        this(from, activity, activity.getWindow().getDecorView(), checkDto);
        updateColorsSheme(buttonColors);
    }

    public CheckBottomPanelController(final FROM from, final BaseActivity activity, final CheckDto checkDto) {
        this(from, activity, activity.getWindow().getDecorView(), checkDto);
    }

    private void updateColorsSheme(ButtonColors buttonColors) {
        if (activity.get() != null && buttonColors != null && ButtonColors.GRAY.name().equals(buttonColors.name())) {
            btnLikes.setImageResource(R.drawable.ic_like_gray);
            likesCountText.setTextColor(activity.get().getResources().getColor(R.color.vote_check_description_color));
            sharesCount.setTextColor(activity.get().getResources().getColor(R.color.vote_check_description_color));
            btnShare.setImageResource(R.drawable.btn_share_gray);
            commentsCount.setTextColor(activity.get().getResources().getColor(R.color.vote_check_description_color));
            btnComments.setImageResource(R.drawable.ic_g_comments);
            peoplesCount.setTextColor(activity.get().getResources().getColor(R.color.vote_check_description_color));
            btnPeoplesCount.setImageResource(R.drawable.ic_g_mob_normal);
        }
    }

    @Override
    public void onClick(View view) {
        if (activity != null && activity.get() != null) {
            if (view.getId() == R.id.btn_share) {
                //TODO not to implement
            } else if (view.getId() == R.id.btn_likes) {
                if (settingsHelper.isLoggedIn()) {
                    if (checkDto != null && checkDto.getWinnerPhotoDto() != null) {
                        serviceHelper.likePhoto(checkDto.getWinnerPhotoDto().getId());
                    } else if (photoDto != null) {
                        serviceHelper.likePhoto(photoDto.getId());
                    }
                } else {
                    activity.get().startActivity(new Intent(activity.get(), LoginActivity.class));
                }
            } else if (view.getId() == R.id.btn_comments) {
                if (photoDto != null) {
                    activity.get().startActivity(CommentsActivity.buildPhotoIntent(activity.get(), photoDto.getId()));
                } else if (checkDto != null && checkDto.getWinnerPhotoDto() != null) {
                    activity.get().startActivity(CommentsActivity.buildPhotoIntent(activity.get(), checkDto.getWinnerPhotoDto().getId()));
                }
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

