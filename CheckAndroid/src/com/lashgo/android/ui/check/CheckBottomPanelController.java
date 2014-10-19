package com.lashgo.android.ui.check;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.settings.SettingsHelper;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.comments.CommentsActivity;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;

import javax.inject.Inject;
import java.lang.ref.WeakReference;

/**
 * Created by Eugene on 20.08.2014.
 */
public class CheckBottomPanelController implements View.OnClickListener {

    @Inject
    SettingsHelper settingsHelper;

    private CheckDto checkDto;

    private WeakReference<BaseActivity> activity;

    private TextView peoplesCount;

    private ImageView btnPeoplesCount;

    private TextView commentsCount;

    private ImageView btnComments;

    private PhotoDto photoDto;

    private void commonInit(final BaseActivity activity, final View view) {
        activity.inject(this);
        this.activity = new WeakReference<BaseActivity>(activity);
        btnPeoplesCount = (ImageView) view.findViewById(R.id.btn_peoples_count);
        btnPeoplesCount.setOnClickListener(this);
        peoplesCount = ((TextView) view.findViewById(R.id.peoples_count));
        commentsCount = ((TextView) view.findViewById(R.id.comments_count));
        btnComments = (ImageView) view.findViewById(R.id.btn_comments);
        btnComments.setOnClickListener(this);
    }

    public CheckBottomPanelController(final BaseActivity activity, View view, final PhotoDto photoDto) {
        commonInit(activity, view);
        this.photoDto = photoDto;
        if (photoDto == null) {
            throw new IllegalArgumentException("Photo info can't be empty!");
        }
        view.findViewById(R.id.time_layout).setVisibility(View.GONE);
        view.findViewById(R.id.peoples_layout).setVisibility(View.GONE);
        commentsCount.setText(String.valueOf(photoDto.getCommentsCount()));
    }

    public CheckBottomPanelController(final BaseActivity activity, final View view, final CheckDto checkDto) {
        commonInit(activity, view);
        this.checkDto = checkDto;
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        peoplesCount.setText(String.valueOf(checkDto.getPlayersCount()));
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        /**
         * we are at check screen
         */
        if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
            /**
             * check is active
             */
            view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
            final TextView checkTimeText = (TextView) view.findViewById(R.id.check_time);
            if (checkDto.getStartDate() != null) {
                long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS;
                if (finishMillis > System.currentTimeMillis()) {
                    UiUtils.startTimer(finishMillis, checkTimeText, new TimerFinishedListener() {
                        @Override
                        public void onTimerFinished() {
                            view.findViewById(R.id.time_layout).setVisibility(View.GONE);
                            if (CheckBottomPanelController.this.activity.get() != null && CheckBottomPanelController.this.activity.get() instanceof CheckActivity) {
                                ((CheckActivity) CheckBottomPanelController.this.activity.get()).onTimerFinished(CheckActivity.TO.VOTE);
                            }

                        }
                    });
                }
            }
        } else {
            /**
             * check is finished
             */
            view.findViewById(R.id.comments_layout).setVisibility(View.GONE);
            view.findViewById(R.id.time_layout).setVisibility(View.GONE);
            view.findViewById(R.id.check_time).setVisibility(View.GONE);
        }
    }

    public CheckBottomPanelController(final BaseActivity activity, final CheckDto checkDto) {
        this(activity, activity.getWindow().getDecorView(), checkDto);
    }

    @Override
    public void onClick(View view) {
        if (activity != null && activity.get() != null) {
            if (view.getId() == R.id.btn_comments) {
                if (photoDto != null) {
                    activity.get().startActivity(CommentsActivity.buildPhotoIntent(activity.get(), photoDto.getId()));
                } else {
                    throw new IllegalStateException("Photo can't be null at comments click");
                }
            } else if (view.getId() == R.id.btn_peoples_count) {
                //TODO open players list
//                activity.get().startActivity(CommentsActivity.buildPhotoIntent(activity.get(), photoDto.getId()));
            }
        }
    }
}

