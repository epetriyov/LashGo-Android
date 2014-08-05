package com.lashgo.android.ui.check;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int CHECH_PHOTO_PADDINGS = 130;
    private CheckDto checkDto;
    private TextView checkTimeText;
    private String imgPath;

    public static Intent newIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckInfoActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_check_info);
        initCheckDto(savedInstanceState);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        checkTimeText = (TextView) findViewById(R.id.check_time);
        ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
        ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
        int imageSize = PhotoUtils.convertPixelsToDp(PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS, this);
        if (!TextUtils.isEmpty(checkDto.getPhotoUrl())) {
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getPhotoUrl())).centerInside().
                    resize(imageSize, imageSize).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.check_photo));
        }
        ((TextView) findViewById(R.id.shares_count)).setText(String.valueOf(checkDto.getSharesCount()));
        ((TextView) findViewById(R.id.peoples_count)).setText(String.valueOf(checkDto.getPlayersCount()));
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

                    }
                }.start();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_camera) {
            DialogFragment makePhotoFragment = new MakePhotoDialog();
            makePhotoFragment.show(getFragmentManager(), MakePhotoDialog.TAG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == MakePhotoDialog.PICK_IMAGE) {
                if (data != null) {
                    imgPath = PhotoUtils.getAbsolutePath(this, data.getData());
                    addMinePhoto();
                } else {
                    Toast.makeText(this, R.string.empty_image_was_chosen, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == MakePhotoDialog.CAPTURE_IMAGE) {
                addMinePhoto();
            }
        }
    }

    private void addMinePhoto() {

    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }
}
