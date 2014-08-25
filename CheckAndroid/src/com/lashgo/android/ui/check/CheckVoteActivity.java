package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 13.08.2014.
 */
public class CheckVoteActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHECH_PHOTO_PADDINGS = 130;
    private CheckDto checkDto;
    private CheckBottomPanelController bottomPanelController;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckVoteActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_vote);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initCheckDto(savedInstanceState);
        initViews();
        bottomPanelController = new CheckBottomPanelController(this, checkDto);
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

    private void initViews() {
        if (checkDto != null) {
            int imageSize = PhotoUtils.convertPixelsToDp(PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS, this);
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerInside().
                        resize(imageSize, imageSize).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.check_photo));
            }
            findViewById(R.id.btn_camera).setOnClickListener(this);
            ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_vote_start) {
            startActivity(FirstVoteActivity.newIntent(this,checkDto));
        } else if (view.getId() == R.id.check_photo) {
            startActivity(PhotoActivity.newIntent(this, checkDto.getTaskPhotoUrl(), checkDto));
        }
    }
}
