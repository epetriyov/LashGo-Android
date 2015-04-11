package com.lashgo.android.ui.check;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.photo.PhotoLoadListener;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;

import java.io.File;

/**
 * Created by Eugene on 30.11.2014.
 */
public class CheckFragment extends BaseFragment implements View.OnClickListener, ICheckFragment {

    public static final int CHECH_PHOTO_PADDINGS = 130;

    private CheckDto checkDto;

    private View winnerMedal;

    private TextView winnerName;

    private int position;

    private ImageView checkImage;

    private View btnSend;

    private int imageSize;

    private String imgPath;

    public static CheckFragment newInstance(CheckDto checkDto, int position, String imgPath) {
        CheckFragment checkFragment = new CheckFragment();
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.ExtraNames.CHECK_DTO.name(), checkDto);
        args.putInt(BaseActivity.ExtraNames.POSITION.name(), position);
        args.putString(BaseActivity.ExtraNames.IMAGE_PATH.name(), imgPath);
        checkFragment.setArguments(args);
        return checkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            checkDto = (CheckDto) args.getSerializable(BaseActivity.ExtraNames.CHECK_DTO.name());
            position = args.getInt(BaseActivity.ExtraNames.POSITION.name());
            imgPath = args.getString(BaseActivity.ExtraNames.IMAGE_PATH.name());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        imageSize = PhotoUtils.getScreenWidth(getActivity()) - CHECH_PHOTO_PADDINGS;
        final View view = inflater.inflate(R.layout.adt_check_pager, container, false);
        checkImage = (ImageView) view.findViewById(R.id.check_photo);
        btnSend = view.findViewById(R.id.btn_send_photo);
        winnerMedal = view.findViewById(R.id.winner_medal);
        winnerName = (TextView) view.findViewById(R.id.winner_name);
        checkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 1 && checkDto.getWinnerPhotoDto() != null) {
                    checkDto.getWinnerPhotoDto().setUser(checkDto.getWinnerInfo());
                    startActivity(CheckPhotoActivity.buildIntent(getActivity(), checkDto));
                } else {
                    ((ICheckActivity) getActivity()).loadExpandedImage(position, checkImage);
                }
            }
        });
        btnSend.setOnClickListener(this);
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        if (position == 0) {
            btnSend.setVisibility(View.GONE);
            winnerMedal.setVisibility(View.GONE);
            winnerName.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                PhotoUtils.displayImage(getActivity(), checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, true, checkState.equals(LashgoConfig.CheckState.VOTE) ? true : false, new PhotoLoadListener() {
                    @Override
                    public void onPhotoLoaded() {
                        view.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else if (position == 1) {
            updateSecondPositionView(view);
        }
        return view;
    }

    private void updateSecondPositionView(final View view) {
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
            if (checkDto.getUserPhotoDto() != null) {
                btnSend.setVisibility(View.GONE);
            } else {
                btnSend.setVisibility(View.VISIBLE);
            }
            if (checkDto.getUserPhotoDto() != null && !TextUtils.isEmpty(checkDto.getUserPhotoDto().getUrl())) {
                PhotoUtils.displayImage(getActivity(), checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getUserPhotoDto().getUrl()), imageSize, true, false, new PhotoLoadListener() {
                    @Override
                    public void onPhotoLoaded() {
                        view.setVisibility(View.VISIBLE);
                    }
                });
            } else if (!TextUtils.isEmpty(imgPath)) {
                PhotoUtils.displayImage(getActivity(), checkImage, Uri.fromFile(new File(imgPath)), imageSize, true, false, new PhotoLoadListener() {
                    @Override
                    public void onPhotoLoaded() {
                        view.setVisibility(View.VISIBLE);
                    }
                });
            }
            winnerMedal.setVisibility(View.GONE);
            winnerName.setVisibility(View.GONE);
        } else if (LashgoConfig.CheckState.FINISHED.equals(checkState)) {
            winnerMedal.setVisibility(View.VISIBLE);
            winnerName.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
            winnerName.setText(TextUtils.isEmpty(checkDto.getWinnerInfo().getFio()) ? checkDto.getWinnerInfo().getLogin() : checkDto.getWinnerInfo().getFio());
            if (checkDto.getWinnerPhotoDto() != null && !TextUtils.isEmpty(checkDto.getWinnerPhotoDto().getUrl())) {
                PhotoUtils.displayImage(getActivity(), checkImage, PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoDto().getUrl()), imageSize, true, true, new PhotoLoadListener() {
                    @Override
                    public void onPhotoLoaded() {
                        view.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_photo) {
            if (!TextUtils.isEmpty(imgPath) && new File(imgPath).exists()) {
                serviceHelper.sendPhoto(checkDto.getId(), imgPath);
            } else {
                ContextUtils.showToast(getActivity(), R.string.error_send_photo);
            }
        }
    }

    public void hideSendPhotoBtn() {
        btnSend.setVisibility(View.GONE);
    }

    public void updateImage(String imgPath) {
        this.imgPath = imgPath;
        if (!TextUtils.isEmpty(imgPath)) {
            PhotoUtils.displayImage(getActivity(), checkImage, Uri.fromFile(new File(imgPath)), imageSize, true, false, new PhotoLoadListener() {
                @Override
                public void onPhotoLoaded() {
                    getView().setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void updateCheckDto(CheckDto checkDto) {
        this.checkDto = checkDto;
        if (position == 1) {
            updateSecondPositionView(getView());
        }
    }
}
