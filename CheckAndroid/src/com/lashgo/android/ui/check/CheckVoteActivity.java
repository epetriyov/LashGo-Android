package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.android.utils.UiUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckVoteActivity extends CheckBaseActivity implements View.OnClickListener {
    private static final int CHECH_PHOTO_PADDINGS = 130;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_vote);
        initViews();
    }

    private void initViews() {
        if (checkDto != null) {
            int imageSize = PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS;
            ImageView checkPhoto = (ImageView) findViewById(R.id.check_photo);
            checkPhoto.setOnClickListener(this);
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerCrop().
                        resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkPhoto);
            }
            findViewById(R.id.btn_vote_start).setOnClickListener(this);
            ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
            final TextView voteCheckTime = ((TextView) findViewById(R.id.vote_check_time));
            long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS + checkDto.getVoteDuration() * DateUtils.HOUR_IN_MILLIS;
            UiUtils.startTimer(finishMillis, voteCheckTime, new TimerFinishedListener() {
                @Override
                public void onTimerFinished() {
                    startActivity(CheckFinishedActivity.buildIntent(CheckVoteActivity.this,
                            checkDto, CheckFinishedActivity.class));
                    finish();
                }
            });
            initBottomPanel();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_vote_start) {
            startActivity(VoteProcessActivity.buildIntent(this, checkDto, VoteProcessActivity.class));
        } else if (view.getId() == R.id.check_photo) {
            startActivity(CheckPhotoActivity.newIntent(this, checkDto.getTaskPhotoUrl(), checkDto));
        }
    }
}
