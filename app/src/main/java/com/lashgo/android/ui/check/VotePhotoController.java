package com.lashgo.android.ui.check;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.lashgo.android.R;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.VotePhoto;

/**
 * Created by Eugene on 25.08.2014.
 */
public class VotePhotoController implements View.OnClickListener {

    private final int imageWidth;
    private final int imageHeight;
    private View photoLayout;

    private ImageView photoImg;

    private ImageView photoCheck;

    private View photoShadow;

    private boolean voteDone;

    private boolean isChecked;

    private VotePhotoListener votePhotoListener;

    public VotePhotoController(Context context, VotePhotoListener votePhotoListener, View rootLayout, int photoLayoutId, int photoImageId, int photoCheckId, int photoShadowId, int imageWidth, int imageHeight, VotePhoto votePhoto) {
        if (rootLayout == null) {
            throw new IllegalArgumentException("Root layout can't be null");
        }
        if (votePhotoListener == null) {
            throw new IllegalArgumentException("Vote photo listener can't be null");
        }
        this.votePhotoListener = votePhotoListener;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        photoLayout = rootLayout.findViewById(photoLayoutId);
        photoLayout.setOnClickListener(this);
        photoLayout.setVisibility(View.INVISIBLE);
        photoImg = (ImageView) rootLayout.findViewById(photoImageId);
        photoCheck = (ImageView) rootLayout.findViewById(photoCheckId);
        photoCheck.setOnClickListener(this);
        photoShadow = rootLayout.findViewById(photoShadowId);
        if (votePhoto != null) {
            setImage(context, votePhoto.getPhotoDto().getUrl());
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setImage(Context context, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            PhotoUtils.displayImage(context, photoImg, PhotoUtils.getFullPhotoUrl(imageUrl), imageWidth, imageHeight);
            photoLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void voteDone() {
        voteDone = true;
        if (isChecked) {
            photoCheck.setImageResource(R.drawable.ic_green_check_pressed);
            votePhotoListener.showOtherShadows(this);
        }
    }

    public void clearCheck() {
        updateCheckState(false);
    }

    public void showShadow() {
        photoShadow.setVisibility(View.VISIBLE);
    }

    private void updateCheckState(boolean isChecked) {
        this.isChecked = isChecked;
        photoCheck.setImageResource(isChecked ? R.drawable.ic_check_focused : R.drawable.ic_check_nornal);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(photoCheck) && !voteDone) {
            updateCheckState(!isChecked);
            if (isChecked) {
                photoShadow.setVisibility(View.GONE);
                votePhotoListener.clearOtherChecks(this);
            } else {
                votePhotoListener.setFirstHint();
            }
        } else if (view.equals(photoLayout)) {
            votePhotoListener.openPhotoActivity(this);
        }
    }

    public View getImageView() {
        return photoImg;
    }

    public static interface VotePhotoListener {
        void clearOtherChecks(VotePhotoController votePhotoController);

        void openPhotoActivity(VotePhotoController votePhotoController);

        void showOtherShadows(VotePhotoController votePhotoController);

        void setFirstHint();
    }
}
