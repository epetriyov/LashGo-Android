package com.lashgo.android.ui.check;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.model.dto.CheckDto;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 20.08.2014.
 */
public class CheckBottomPanelController implements View.OnClickListener {

    public CheckBottomPanelController(final Activity activity, final CheckDto checkDto) {
        if (checkDto == null) {
            throw new IllegalArgumentException("Check can't be empty!");
        }
        activity.findViewById(R.id.btn_share).setOnClickListener(this);
        ((TextView) activity.findViewById(R.id.shares_count)).setText(String.valueOf(checkDto.getSharesCount()));
        ((TextView) activity.findViewById(R.id.peoples_count)).setText(String.valueOf(checkDto.getPlayersCount()));
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
                    new CountDownTimer(finishMillis - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                            long remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
                            checkTimeText.setText(String.valueOf(remainingMinutes) + ":" + String.valueOf(remainingSeconds));
                        }

                        @Override
                        public void onFinish() {
                            activity.startActivity(CheckVoteActivity.buildIntent(activity,
                                    checkDto));
                            activity.finish();
                        }
                    }.start();
                }
            }
        } else {
            ((TextView) activity.findViewById(R.id.likes_count)).setText(String.valueOf(checkDto.getLikesCount()));
            ((TextView) activity.findViewById(R.id.comments_count)).setText(String.valueOf(checkDto.getCommentsCount()));
            activity.findViewById(R.id.btn_likes).setOnClickListener(this);
            activity.findViewById(R.id.btn_comments).setOnClickListener(this);
            activity.findViewById(R.id.check_time).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_share) {

        } else if (view.getId() == R.id.btn_likes) {

        } else if (view.getId() == R.id.btn_comments) {

        }
    }
}
