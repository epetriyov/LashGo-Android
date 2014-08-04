package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckInfoActivity extends BaseActivity {

    private static final int CHECH_PHOTO_PADDINGS = 130;
    private CheckDto checkDto;

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
        ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
        ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
        int imageSize = PhotoUtils.convertPixelsToDp(PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS, this);
        if (!TextUtils.isEmpty(checkDto.getPhotoUrl())) {
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getPhotoUrl())).centerInside().
                    resize(imageSize, imageSize).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.check_photo));
        }
        ((TextView) findViewById(R.id.shares_count)).setText(String.valueOf(checkDto.getSharesCount()));
        ((TextView) findViewById(R.id.peoples_count)).setText(String.valueOf(checkDto.getPlayersCount()));
    }
}
