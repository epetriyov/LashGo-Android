package com.lashgo.android.ui.profile;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.check.ActivityReferrer;
import com.lashgo.android.ui.check.PhotoActivity;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.SubscribeDto;
import com.lashgo.model.dto.UserDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 10.08.2014.
 */
public class ProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private UserDto userDto;

    private ImageView editView;
    private MenuItem editMenu;

    private ArrayList<PhotoDto> photosList;

    private int localUserSubscibersCount;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(PhotoActivity.buildIntent(this, photosList, i, ActivityReferrer.FROM_PROFILE_GALLERY.name()));
    }

    private PhotoGalleryAdapter photoGalleryAdapter;

    private int userId;

    public static Intent buildIntent(Context context, int userId) {
        Intent intent = new Intent(context, ProfileActivity.class);
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
        outState.putInt(ExtraNames.USER_ID.name(), userId);
        super.onSaveInstanceState(outState);
    }

    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            userId = savedInstanceState.getInt(ExtraNames.USER_ID.name(), -1);
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                userId = intent.getIntExtra(ExtraNames.USER_ID.name(), -1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP);
        initExtras(savedInstanceState);
        setContentView(R.layout.act_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        if (userId == settingsHelper.getUserId()) {
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
        editMenu = menu.findItem(R.id.action_edit);
        editView = (ImageView) editMenu.getActionView();
        editMenu.setVisible(false);
        return true;
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE && data != null) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name().equals(action)) {
                UserDto loadedUser = (UserDto) data.getSerializable(BaseIntentHandler.ServiceExtraNames.USER_PROFILE.name());
                initViews(loadedUser);
                if (BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name().equals(action)) {
                    editView.setImageResource(R.drawable.ic_action_edit);
                    editView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(EditProfileActivity.buildIntent(ProfileActivity.this, userDto, EditProfileActivity.FROM.PROFILE));
                        }
                    });
                } else {
                    if (loadedUser.isSubscription()) {
                        editView.setImageResource(R.drawable.ic_subscribed);
                        editView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                serviceHelper.unsubscribe(userId);
                            }
                        });
                    } else {
                        editView.setImageResource(R.drawable.ic_subscribe);
                        editView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                serviceHelper.subscribe(new SubscribeDto(userId));
                            }
                        });
                    }
                }
                editMenu.setVisible(true);
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name().equals(action)) {
                photosList = new ArrayList<>((ArrayList<PhotoDto>) data.getSerializable(BaseIntentHandler.ServiceExtraNames.PHOTOS_LIST.name()));
                initGallery(photosList);
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name().equals(action)) {
                localUserSubscibersCount++;
                updateSubscribersCount();
                editView.setImageResource(R.drawable.ic_subscribed);
                editView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serviceHelper.unsubscribe(userId);
                    }
                });
            } else if (BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name().equals(action)) {
                localUserSubscibersCount--;
                updateSubscribersCount();
                editView.setImageResource(R.drawable.ic_subscribe);
                editView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serviceHelper.subscribe(new SubscribeDto(userId));
                    }
                });
            }
        }
    }

    private void initGallery(ArrayList<PhotoDto> photoDtos) {
        if (photoDtos != null) {
            photoGalleryAdapter.clear();
            for (PhotoDto photoDto : photoDtos) {
                photoGalleryAdapter.add(photoDto);
            }
            photoGalleryAdapter.notifyDataSetChanged();
        }
    }

    private void updateSubscribersCount()
    {
        ((TextView) findViewById(R.id.user_subscribers)).setText(String.valueOf(localUserSubscibersCount));
    }

    private void initViews(UserDto userDto) {
        this.userDto = userDto;
        this.localUserSubscibersCount = userDto.getUserSubscribers();
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
        GridView photosGallery = (GridView) findViewById(R.id.photos_galley);
        int imageSize = (PhotoUtils.getScreenWidth(this) - 20) / 2;
        photosGallery.setOnItemClickListener(this);
        photoGalleryAdapter = new PhotoGalleryAdapter(this, imageSize);
        photosGallery.setAdapter(photoGalleryAdapter);
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PROFILE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_USER_PROFILE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_USER_PHOTOS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_MY_PHOTOS.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SUBSCRIBE.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_UNSUBSCRIBE.name());
    }
}
