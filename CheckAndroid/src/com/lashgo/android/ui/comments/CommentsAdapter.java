package com.lashgo.android.ui.comments;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CommentDto;

import java.text.SimpleDateFormat;

/**
 * Created by Eugene on 11.09.2014.
 */
public class CommentsAdapter extends ArrayAdapter<CommentDto> {

    private static class ViewHolder {
        private ImageView userAvatar;
        private TextView userName;
        private TextView commentText;
        private TextView commentDate;
    }

    public CommentsAdapter(Context context) {
        super(context, -1);
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adt_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.user_avatar);
            viewHolder.commentDate = (TextView) convertView.findViewById(R.id.comment_date);
            viewHolder.commentText = (TextView) convertView.findViewById(R.id.comment_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommentDto commentDto = getItem(position);
        if (commentDto.getUser() != null) {
            viewHolder.userName.setText(commentDto.getUser().getLogin());
            if (!TextUtils.isEmpty(commentDto.getUser().getAvatar())) {
                int imageSize = PhotoUtils.convertDpToPixels(40, getContext());
                PhotoUtils.displayImage(getContext(), viewHolder.userAvatar, LashGoUtils.getUserAvatarUrl(commentDto.getUser().getAvatar()), imageSize, R.drawable.ava, false);
            }
        }
        viewHolder.commentText.setText(commentDto.getContent());
        if (commentDto.getCreateDate() != null) {
            viewHolder.commentDate.setText(dateFormat.format(commentDto.getCreateDate()));
        }
        return convertView;
    }
}
