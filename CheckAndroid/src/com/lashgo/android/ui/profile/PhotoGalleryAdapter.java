package com.lashgo.android.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.lashgo.android.R;

/**
 * Created by Eugene on 27.08.2014.
 */
public class PhotoGalleryAdapter extends ArrayAdapter<PhotoItem> {

    private static class ViewHolder
    {
        private ImageView photoView;

        private ImageView winnerIcon;

        private ImageView bannedIcon;
    }

    public PhotoGalleryAdapter(Context context) {
        super(context,-1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adt_profile_photo,null);
            viewHolder.photoView = (ImageView) convertView.findViewById(R.id.photo);
            viewHolder.winnerIcon = (ImageView) convertView.findViewById(R.id.winner_icon);
            viewHolder.bannedIcon = (ImageView) convertView.findViewById(R.id.banned_icon);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
