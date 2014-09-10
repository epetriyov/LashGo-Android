package com.lashgo.android.ui.profile;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.check.MakePhotoDialog;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.Md5Util;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.UserDto;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Eugene on 10.09.2014.
 */
public class EditProfileActivity extends BaseActivity implements View.OnClickListener, MakePhotoDialog.OnImageDoneListener {

    private int avatarSize;

    private boolean profileSaved;

    private boolean avatarSaved;

    private ImageView userAvatar;

    private UserDto userDto;

    private EditText editFio;

    private EditText editLocation;

    private EditText editEmail;

    private EditText editAbout;

    private EditText editPassword;
    private String imgPath;

    public static Intent buildIntent(Context context, UserDto userDto) {
        Intent intent = new Intent(context, EditProfileActivity.class);
        intent.putExtra(ExtraNames.USER_DTO.name(), userDto);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.USER_DTO.name(), userDto);
        outState.putString(ExtraNames.PHOTO_URL.name(), imgPath);
        super.onSaveInstanceState(outState);
    }

    private void initUserDto(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            userDto = (UserDto) intent.getSerializableExtra(ExtraNames.USER_DTO.name());
        }
        if (savedInstanceState != null && userDto == null) {
            userDto = (UserDto) savedInstanceState.getSerializable(ExtraNames.USER_DTO.name());
            imgPath = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
        }
        if (userDto == null) {
            throw new IllegalStateException("UserDto can't be empty");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initUserDto(savedInstanceState);
        setContentView(R.layout.act_edit_profile);
        initViews();
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_AVATAR.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_PROFILE.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_AVATAR.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SAVE_PROFILE.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_SAVE_PROFILE.name().equals(action)) {
                profileSaved = true;
                if (avatarSaved || imgPath == null) {
                    profileSaved();
                }
            } else {
                avatarSaved = true;
                if (profileSaved) {
                    profileSaved();
                }
            }
        }
    }

    private void sendProfile() {
        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            editEmail.setError(getString(R.string.error_empty_email));
        } else {
            if (imgPath != null) {
                serviceHelper.saveAvatar(imgPath);
            }
            boolean isProfileChanged = false;
            if (!userDto.getFio().equals(editFio.getText().toString())) {
                userDto.setFio(editFio.getText().toString());
                isProfileChanged = true;
            }
            if (!userDto.getCity().equals(editLocation.getText().toString())) {
                userDto.setCity(editLocation.getText().toString());
                isProfileChanged = true;
            }
            if (!userDto.getEmail().equals(editEmail.getText().toString())) {
                userDto.setEmail(editEmail.getText().toString());
                isProfileChanged = true;
            }
            if (!userDto.getAbout().equals(editAbout.getText().toString())) {
                userDto.setAbout(editAbout.getText().toString());
                isProfileChanged = true;
            }
            if (!userDto.getPasswordHash().equals(editPassword.getText().toString()) && !TextUtils.isEmpty(editPassword.getText().toString())) {
                userDto.setPasswordHash(Md5Util.md5(editPassword.getText().toString()));
                isProfileChanged = true;
            }
            if (isProfileChanged) {
                serviceHelper.saveProfile(userDto);
            }
        }
    }

    private void profileSaved() {
        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_LONG).show();
        finish();
    }

    private void initViews() {
        userAvatar = (ImageView) findViewById(R.id.user_avatar);
        avatarSize = PhotoUtils.convertDpToPixels(64, this);
        if (!TextUtils.isEmpty(imgPath)) {
            addMinePhoto();
        } else if (!TextUtils.isEmpty(userDto.getAvatar())) {
            Picasso.with(this).load(PhotoUtils.getFullPhotoUrl(userDto.getAvatar())).centerInside().
                    resize(avatarSize, avatarSize).transform(new CircleTransformation()).error(R.drawable.ava).placeholder(R.drawable.ava).into(userAvatar);
        }
        editFio = (EditText) findViewById(R.id.edit_fio);
        editLocation = (EditText) findViewById(R.id.edit_location);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editAbout = (EditText) findViewById(R.id.edit_about);
        editPassword = (EditText) findViewById(R.id.edit_password);
        if (!TextUtils.isEmpty(userDto.getFio())) {
            editFio.setText(userDto.getFio());
        }
        if (!TextUtils.isEmpty(userDto.getCity())) {
            editLocation.setText(userDto.getCity());
        }
        if (!TextUtils.isEmpty(userDto.getEmail())) {
            editEmail.setText(userDto.getEmail());
        }
        if (!TextUtils.isEmpty(userDto.getAbout())) {
            editAbout.setText(userDto.getAbout());
        }
        findViewById(R.id.make_photo).setOnClickListener(this);
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
        if (view.getId() == R.id.make_photo) {
            DialogFragment makePhotoFragment = new MakePhotoDialog(this);
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
        Picasso.with(this).load(Uri.fromFile(new File(imgPath))).centerInside().
                resize(avatarSize, avatarSize).transform(new CircleTransformation()).error(R.drawable.ava).placeholder(R.drawable.ava).into(userAvatar);
    }

    @Override
    public void imageDone(String imagePath) {
        this.imgPath = imagePath;
    }
}
