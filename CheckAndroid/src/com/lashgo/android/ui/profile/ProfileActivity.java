package com.lashgo.android.ui.profile;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.check.PhotoActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.UserDto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Eugene on 10.08.2014.
 */
public class ProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private UserDto userDto;

    private ImageView editView;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(PhotoActivity.newIntent(this, photoGalleryAdapter.getItem(i), PhotoActivity.PhotoType.FROM_PROFILE_GALLERY));
    }

    private PhotoGalleryAdapter photoGalleryAdapter;

    public static enum ProfileOwner {ME, OTHERS}

    private ProfileOwner profileOwner;

    private int userId;

    public static Intent buildIntent(Context context, ProfileOwner profileOwner) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ExtraNames.PROFILE_OWNER.name(), profileOwner);
        return intent;
    }

    public static Intent buildIntent(Context context, ProfileOwner profileOwner, int userId) {
        Intent intent = buildIntent(context, profileOwner);
        intent.putExtra(ExtraNames.USER_ID.name(), userId);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadProfile();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.PROFILE_OWNER.name(), profileOwner);
        outState.putInt(ExtraNames.USER_ID.name(), userId);
        super.onSaveInstanceState(outState);
    }

    private void initExtras(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            profileOwner = (ProfileOwner) intent.getSerializableExtra(ExtraNames.PROFILE_OWNER.name());
            userId = intent.getIntExtra(ExtraNames.USER_ID.name(), -1);
        }
        if (savedInstanceState != null) {
            if (profileOwner == null) {
                profileOwner = (ProfileOwner) savedInstanceState.getSerializable(ExtraNames.PROFILE_OWNER.name());
            }
            if (userId <= 0) {
                userId = savedInstanceState.getInt(ExtraNames.USER_ID.name(), -1);
            }
        }
        if (profileOwner == null) {
            throw new IllegalStateException("Profile owner can't be empty");
        }
    }

    @Override
    public void onUpClicked() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_profile);
        loadProfile();
    }

    private void loadProfile()
    {
        if (ProfileOwner.ME.name().equals(profileOwner.name())) {
            serviceHelper.getMyUserProfile();
            serviceHelper.getMyPhotos();
        } else {
            serviceHelper.getUserProfile(userId);
            serviceHelper.getUserPhotos(userId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        editView = (ImageView) menu.findItem(R.id.action_edit).getActionView();
        editView.setImageResource(R.drawable.ic_action_edit);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(EditProfileActivity.buildIntent(ProfileActivity.this, userDto, EditProfileActivity.FROM.PROFILE));
            }
        });
        editView.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE && data != null) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name().equals(action)) {
                initViews((UserDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.USER_PROFILE.name()));
                editView.setVisibility(View.VISIBLE);
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name().equals(action)) {
                initGallery((ArrayList<PhotoDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.PHOTOS_LIST.name()));
            }
        }
    }

    private void initGallery(ArrayList<PhotoDto> photoDtos) {
        if (photoDtos != null) {
            GridView photosGallery = (GridView) findViewById(R.id.photos_galley);
            int imageSize = (PhotoUtils.getScreenWidth(this) - 20) / 2;
            photosGallery.setOnItemClickListener(this);
            photoGalleryAdapter = new PhotoGalleryAdapter(this, imageSize);
            photosGallery.setAdapter(photoGalleryAdapter);
            for (PhotoDto photoDto : photoDtos) {
                photoGalleryAdapter.add(photoDto);
            }
            photoGalleryAdapter.notifyDataSetChanged();
        }
    }

    private void initViews(UserDto userDto) {
        this.userDto = userDto;
        if (userDto != null) {
            int imageSize = PhotoUtils.convertDpToPixels(64, this);
            if (!TextUtils.isEmpty(userDto.getAvatar())) {
                PhotoUtils.displayImage(this, ((ImageView) findViewById(R.id.user_avatar)), LashGoUtils.getUserAvatarUrl(userDto.getAvatar()), imageSize, R.drawable.ava, false);
            }
            ((TextView) findViewById(R.id.user_subscribes)).setText(String.valueOf(userDto.getUserSubscribes()));
            ((TextView) findViewById(R.id.user_subscribers)).setText(String.valueOf(userDto.getUserSubscribers()));
            ((TextView) findViewById(R.id.user_name)).setText(!TextUtils.isEmpty(userDto.getFio()) ? userDto.getFio() : userDto.getLogin());
            ((TextView) findViewById(R.id.checks_count)).setText(String.format(getString(R.string.checks_count), userDto.getChecksCount()));
            ((TextView) findViewById(R.id.comments_count)).setText(String.format(getString(R.string.comments_count), userDto.getCommentsCount()));
            ((TextView) findViewById(R.id.likes_count)).setText(String.format(getString(R.string.likes_count), userDto.getLikesCount()));
        }
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name());
    }
}
