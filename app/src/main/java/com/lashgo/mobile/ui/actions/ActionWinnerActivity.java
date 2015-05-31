package com.lashgo.mobile.ui.actions;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.mobile.LashgoConfig;
import com.lashgo.mobile.R;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.ui.views.GradientImageView;
import com.lashgo.mobile.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;

import java.text.DateFormat;

/**
 * Created by Eugene on 30.05.2015.
 */
public class ActionWinnerActivity extends BaseActivity {

    private CheckDto checkDto;

    public static final int CHECH_PHOTO_PADDINGS = 132;

    private static final int CHECK_PADDING = 45;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, ActionWinnerActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheck(savedInstanceState);
        setContentView(R.layout.act_action_winner);
        if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            int taskImageSize = PhotoUtils.convertDpToPixels(40, this);
            PhotoUtils.displayImage(this, (ImageView) findViewById(R.id.task_photo), PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), taskImageSize, R.drawable.ava, false);
        }
        ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
        GradientImageView checkGradient = (GradientImageView) findViewById(R.id.check_gradient);
        int imageSize = PhotoUtils.getScreenWidth(this) - PhotoUtils.convertDpToPixels(CHECK_PADDING * 2, this);
        ViewGroup.LayoutParams layoutParams = checkGradient.getLayoutParams();
        layoutParams.height = imageSize;
        layoutParams.width = imageSize;
        checkGradient.setLayoutParams(layoutParams);
        checkGradient.updateImage(LashgoConfig.CheckState.VOTE, 1f);
        imageSize = PhotoUtils.getScreenWidth(this) - PhotoUtils.convertDpToPixels(CHECH_PHOTO_PADDINGS,this);
        ImageView checkImage = (ImageView) findViewById(R.id.check_photo);
        if (checkDto.getWinnerPhotoDto() != null && !TextUtils.isEmpty(checkDto.getWinnerPhotoDto().getUrl())) {
            PhotoUtils.displayImage(this, checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoDto().getUrl()), imageSize, true, true, null);
        }
        TextView winnerName = (TextView) findViewById(R.id.winner_name);
        winnerName.setText(TextUtils.isEmpty(checkDto.getWinnerInfo().getFio()) ? checkDto.getWinnerInfo().getLogin() : checkDto.getWinnerInfo().getFio());
        ((TextView) findViewById(R.id.start_date)).setText(DateFormat.getDateInstance().format(checkDto.getStartDate()));
    }

    private void initCheck(Bundle bundle) {
        Intent intent = getIntent();
        if (intent != null) {
            checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
        }
        if (checkDto == null && bundle != null) {
            checkDto = (CheckDto) bundle.getSerializable(ExtraNames.CHECK_DTO.name());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }
}
