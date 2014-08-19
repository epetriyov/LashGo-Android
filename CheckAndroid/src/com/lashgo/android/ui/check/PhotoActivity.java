package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.ui.views.RobotoTextView;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 18.08.2014.
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener {

    private String photoUrl;
    private CheckDto checkDto;

    private View topPhotoPanel;

    private View bottomBg;

    public static Intent newIntent(Context context, String photoUrl, CheckDto checkDto) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        intent.putExtra(ExtraNames.PHOTO_URL.name(), photoUrl);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ExtraNames.PHOTO_URL.name(), photoUrl);
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    private void initPhotoUrl(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            photoUrl = intent.getStringExtra(ExtraNames.PHOTO_URL.name());
            checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
        }
        if (savedInstanceState != null) {
            if (photoUrl == null) {
                photoUrl = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
            }
            if (checkDto == null) {
                checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPhotoUrl(savedInstanceState);
        setContentView(R.layout.act_photo);
        initViews();
    }

    private void initViews() {
        if (checkDto != null) {
            if (!TextUtils.isEmpty(photoUrl)) {
                ImageView fullImage = ((ImageView) findViewById(R.id.full_photo));
                fullImage.setOnClickListener(this);
                Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(photoUrl)).centerInside().into(fullImage);
            }
            topPhotoPanel = findViewById(R.id.top_check_panel);
            int imageSize = PhotoUtils.convertPixelsToDp(40, this);
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerInside().
                    resize(imageSize, imageSize).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.task_photo));
            ((RobotoTextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            bottomBg = findViewById(R.id.bottom_photo_bg);
            findViewById(R.id.btn_share).setOnClickListener(this);
            ((TextView) findViewById(R.id.shares_count)).setText(String.valueOf(checkDto.getSharesCount()));
            ((TextView) findViewById(R.id.peoples_count)).setText(String.valueOf(checkDto.getPlayersCount()));
            LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
            if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
                findViewById(R.id.likes_layout).setVisibility(View.GONE);
                findViewById(R.id.comments_layout).setVisibility(View.GONE);
                final TextView checkTimeText = (TextView) findViewById(R.id.check_time);
                if (checkDto.getStartDate() != null) {
                    long finishMillis = checkDto.getStartDate().getTime() + checkDto.getDuration() * DateUtils.HOUR_IN_MILLIS;
                    if (finishMillis > System.currentTimeMillis()) {
                        new CountDownTimer(finishMillis - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                                long remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
                                checkTimeText.setText(String.valueOf(remainingMinutes) + ":" + String.valueOf(remainingSeconds));
                            }

                            @Override
                            public void onFinish() {
                                startActivity(CheckVoteActivity.buildIntent(PhotoActivity.this,
                                        checkDto));
                                finish();
                            }
                        }.start();
                    }
                }
            } else {
                ((TextView) findViewById(R.id.likes_count)).setText(String.valueOf(checkDto.getLikesCount()));
                ((TextView) findViewById(R.id.comments_count)).setText(String.valueOf(checkDto.getCommentsCount()));
                findViewById(R.id.btn_likes).setOnClickListener(this);
                findViewById(R.id.btn_comments).setOnClickListener(this);
                findViewById(R.id.check_time).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}
