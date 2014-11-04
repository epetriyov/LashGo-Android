package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.ui.views.RobotoTextView;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckCounters;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;
import com.lashgo.model.dto.UserDto;

/**
 * Created by Eugene on 18.08.2014.
 */
public class PhotoFragment extends BaseFragment implements View.OnClickListener {

    private int imageSize;

    private ImageView fullImage;

    private TextView topText;

    private ImageView taskPhoto;

    private View bottomBg;

    private View topPhotoPanel;

    private PhotoDto photoDto;

    private String activityReferrer;
    private CheckBottomPanelController bottomPanel;

    public static Fragment newInstance(PhotoDto photoDto, String activityReferrer) {
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseActivity.ExtraNames.PHOTO_DTO.name(), photoDto);
        bundle.putString(BaseActivity.ExtraNames.ACTIVITY_REFERRER.name(), activityReferrer);
        photoFragment.setArguments(bundle);
        return photoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (savedInstanceState != null) {
            photoDto = (PhotoDto) savedInstanceState.getSerializable(BaseActivity.ExtraNames.PHOTO_DTO.name());
            activityReferrer = savedInstanceState.getString(BaseActivity.ExtraNames.ACTIVITY_REFERRER.name());
        } else if (args != null) {
            photoDto = (PhotoDto) args.getSerializable(BaseActivity.ExtraNames.PHOTO_DTO.name());
            activityReferrer = args.getString(BaseActivity.ExtraNames.ACTIVITY_REFERRER.name());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BaseActivity.ExtraNames.PHOTO_DTO.name(), photoDto);
        outState.putString(BaseActivity.ExtraNames.ACTIVITY_REFERRER.name(), activityReferrer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adt_photo, container, false);
        initViews(view);
        bottomPanel = new CheckBottomPanelController((BaseActivity) getActivity(), view, photoDto);
        return view;
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_COUNTERS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_COUNTERS.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE && data != null) {
            CheckCounters checkCounters = (CheckCounters) data.getSerializable(BaseIntentHandler.ServiceExtraNames.COUNTERS.name());
            bottomPanel.updateCommentsCount(checkCounters.getCommentsCount());
            bottomPanel.updateLikesCount(checkCounters.getLikesCount());
        }
    }

    @Override
    public void refresh() {
        serviceHelper.getPhotoCounters(photoDto.getId());
    }

    private void setUpTopCheck(CheckDto checkDto) {
        if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            PhotoUtils.displayImage(getActivity(), taskPhoto, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
        }
        topText.setText(checkDto.getName());
    }

    private void initViews(View view) {
        fullImage = ((ImageView) view.findViewById(R.id.full_photo));
        fullImage.setOnClickListener(this);
        topPhotoPanel = view.findViewById(R.id.top_check_panel);
        bottomBg = view.findViewById(R.id.bottom_photo_bg);
        imageSize = PhotoUtils.convertDpToPixels(40, getActivity());
        topText = ((RobotoTextView) view.findViewById(R.id.check_name));
        topText.setOnClickListener(this);
        taskPhoto = (ImageView) view.findViewById(R.id.task_photo);
        taskPhoto.setOnClickListener(this);
        PhotoUtils.displayFullImage(getActivity(), fullImage, PhotoUtils.getFullPhotoUrl(photoDto.getUrl()));
        if (ActivityReferrer.FROM_PROFILE_GALLERY.name().equals(activityReferrer)) {
            setUpTopCheck(photoDto.getCheck());
        } else {
            setUpTopUser(photoDto.getUser());
        }
    }

    private void setUpTopUser(UserDto userDto) {
        if (userDto != null) {
            if (!TextUtils.isEmpty(userDto.getAvatar())) {
                PhotoUtils.displayImage(getActivity(), taskPhoto, LashGoUtils.getUserAvatarUrl(userDto.getAvatar()), imageSize, R.drawable.ava, false);
            }
            topText.setText(TextUtils.isEmpty(userDto.getFio()) ? userDto.getLogin() : userDto.getFio());
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
        } else if (view.getId() == R.id.task_photo || view.getId() == R.id.check_name) {
            if (ActivityReferrer.FROM_CHECK_GALLERY.name().equals(activityReferrer) && photoDto.getUser() != null) {
                startActivity(ProfileActivity.buildIntent(getActivity(), photoDto.getUser().getId()));
            } else if (photoDto.getCheck() != null) {
                startActivity(CheckActivity.buildIntent(getActivity(), photoDto.getCheck().getId()));
            }
        }
    }
}
