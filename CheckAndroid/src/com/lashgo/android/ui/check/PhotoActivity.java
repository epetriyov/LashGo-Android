package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.ui.views.RobotoTextView;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 18.08.2014.
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener {

    private String photoUrl;

    private CheckDto checkDto;

    private CheckBottomPanelController bottomPanelController;

    private View bottomBg;

    private View topPhotoPanel;

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
        bottomPanelController = new CheckBottomPanelController(this, checkDto);
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
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.full_photo) {
            if (topPhotoPanel.getVisibility() == View.VISIBLE) {
                topPhotoPanel.setVisibility(View.GONE);
                bottomBg.setVisibility(View.GONE);
            } else {
                topPhotoPanel.setVisibility(View.VISIBLE);
                bottomBg.setVisibility(View.VISIBLE);
            }
        }
    }
}
