package com.lashgo.android.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.RegisterResponse;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 06.08.2014.
 */
public class SuccessfulRegisterActivity extends BaseActivity implements View.OnClickListener {

    private RegisterResponse registerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_success_register);
        initRegisterResponse(savedInstanceState);
        findViewById(R.id.fill_profile).setOnClickListener(this);
        findViewById(R.id.continue_register).setOnClickListener(this);
        findViewById(R.id.make_photo).setOnClickListener(this);
        if (registerResponse != null) {
            int imageSize = PhotoUtils.convertPixelsToDp(64, this);
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(registerResponse.getAvatar())).centerInside().
                    resize(imageSize, imageSize).transform(new CircleTransformation()).error(R.drawable.ava).placeholder(R.drawable.ava).into((ImageView) findViewById(R.id.user_avatar));
            ((TextView) findViewById(R.id.user_subscribes)).setText(String.valueOf(registerResponse.getSubscribesCount()));
            ((TextView) findViewById(R.id.user_subscribers)).setText(String.valueOf(registerResponse.getSubscribersCount()));
            ((TextView) findViewById(R.id.user_name)).setText(registerResponse.getUserName());
        }
    }

    private void initRegisterResponse(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            registerResponse = (RegisterResponse) intent.getSerializableExtra(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name());
        }
        if (savedInstanceState != null && registerResponse == null) {
            registerResponse = (RegisterResponse) savedInstanceState.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name(), registerResponse);
        super.onSaveInstanceState(outState);
    }

    public static Intent buildIntent(Context context, RegisterResponse registerResponse) {
        Intent intent = new Intent(context, SuccessfulRegisterActivity.class);
        intent.putExtra(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name(), registerResponse);
        return intent;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.continue_register) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (view.getId() == R.id.fill_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }
}
