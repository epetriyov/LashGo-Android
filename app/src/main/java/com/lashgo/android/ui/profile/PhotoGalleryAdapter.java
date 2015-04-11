package com.lashgo.android.ui.profile;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.lashgo.android.R;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.PhotoDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 27.08.2014.
 */
public class PhotoGalleryAdapter extends ArrayAdapter<PhotoDto> {

    private static class ViewHolder {
        private SquaredImageView photoView;

        private ImageView winnerIcon;

        private ImageView bannedIcon;
    }

    private int imageSize;

    public PhotoGalleryAdapter(Context context, int imageSize) {
        super(context, -1);
        this.imageSize = imageSize;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adt_profile_photo, null);
            viewHolder.photoView = (SquaredImageView) convertView.findViewById(R.id.photo);
            viewHolder.winnerIcon = (ImageView) convertView.findViewById(R.id.winner_icon);
            viewHolder.bannedIcon = (ImageView) convertView.findViewById(R.id.banned_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PhotoDto photoItem = getItem(position);
        if (photoItem.isBanned()) {
            viewHolder.bannedIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.bannedIcon.setVisibility(View.GONE);
        }
        if (photoItem.isWinner()) {
            viewHolder.winnerIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.winnerIcon.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(photoItem.getUrl())) {
            Picasso.with(getContext()).load(PhotoUtils.getFullPhotoUrl(photoItem.getUrl())).resize(imageSize,imageSize).centerCrop().into(viewHolder.photoView);
//            PhotoUtils.displayImage(getContext(), viewHolder.photoView, PhotoUtils.getFullPhotoUrl(photoItem.getUrl()), imageSize);
        }
        return convertView;
    }
}
