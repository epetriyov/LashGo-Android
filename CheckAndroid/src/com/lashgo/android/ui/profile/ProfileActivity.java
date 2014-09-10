package com.lashgo.android.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.UserDto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Eugene on 10.08.2014.
 */
public class ProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_profile);
        if (ProfileOwner.ME.name().equals(profileOwner.name())) {
            serviceHelper.getMyUserProfile();
            serviceHelper.getMyPhotos();
        } else {
            serviceHelper.getUserProfile(userId);
            serviceHelper.getUserPhotos(userId);
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE && data != null) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name().equals(action)) {
                initViews((UserDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.USER_PROFILE.name()));
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
        if (userDto != null) {
            int imageSize = PhotoUtils.convertDpToPixels(64, this);
            if (!TextUtils.isEmpty(userDto.getAvatar())) {
                Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(PhotoUtils.getFullPhotoUrl(userDto.getAvatar()))).centerCrop().
                        resize(imageSize, imageSize).transform(new CircleTransformation()).into(((ImageView) findViewById(R.id.user_avatar)));
            }
            ((TextView) findViewById(R.id.user_subscribes)).setText(String.valueOf(userDto.getUserSubscribes()));
            ((TextView) findViewById(R.id.user_subscribers)).setText(String.valueOf(userDto.getUserSubscribers()));
            ((TextView) findViewById(R.id.user_name)).setText(userDto.getFio());
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
