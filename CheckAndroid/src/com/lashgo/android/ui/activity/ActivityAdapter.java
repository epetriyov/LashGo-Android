package com.lashgo.android.ui.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.check.CheckActivity;
import com.lashgo.android.ui.check.PhotoActivity;
import com.lashgo.android.ui.profile.ProfileActivity;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.DbCodes;
import com.lashgo.model.dto.EventDto;

import java.text.SimpleDateFormat;

/**
 * Created by Eugene on 20.10.2014.
 */
public class ActivityAdapter extends ArrayAdapter<EventDto> {

    private static final String EVENT_DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

    private SimpleDateFormat simpleDateFormat;

    private int subscibesCount;

    public ActivityAdapter(Context context, int subscibesCount) {
        super(context, -1);
        simpleDateFormat = new SimpleDateFormat(EVENT_DATE_PATTERN);
        this.subscibesCount = subscibesCount;
    }

    private static class ViewHolder {
        private ImageView pointer;
        private ImageView userAvatar;
        private TextView actionText;
        private TextView eventDate;
        private ImageView objectPhoto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adt_activity, null);
            viewHolder.pointer = (ImageView) convertView.findViewById(R.id.pointer);
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.user_avatar);
            viewHolder.actionText = (TextView) convertView.findViewById(R.id.action_text);
            viewHolder.eventDate = (TextView) convertView.findViewById(R.id.event_date);
            viewHolder.objectPhoto = (ImageView) convertView.findViewById(R.id.object_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final EventDto eventDto = getItem(position);
        if (position < subscibesCount) {
            viewHolder.pointer.setImageResource(R.drawable.led_blue);
        } else {
            viewHolder.pointer.setImageResource(R.drawable.led_gray);
        }

        if (eventDto.getUser() != null) {
            viewHolder.userAvatar.setVisibility(View.VISIBLE);
            viewHolder.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(ProfileActivity.buildIntent(getContext(), eventDto.getUser().getId()));
                }
            });
            if (!TextUtils.isEmpty(eventDto.getUser().getAvatar())) {
                PhotoUtils.displayImage(getContext(), viewHolder.userAvatar, LashGoUtils.getUserAvatarUrl(eventDto.getUser().getAvatar()), PhotoUtils.convertDpToPixels(40, getContext()), R.drawable.ava, false);
            } else {
                viewHolder.userAvatar.setImageResource(R.drawable.ava);
            }
        } else {
            viewHolder.userAvatar.setVisibility(View.GONE);
        }

        if (eventDto.getCheck() != null && !TextUtils.isEmpty(eventDto.getCheck().getTaskPhotoUrl())) {
            viewHolder.objectPhoto.setVisibility(View.VISIBLE);
            viewHolder.objectPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(CheckActivity.buildIntent(getContext(), eventDto.getCheck().getId()));
                }
            });
            PhotoUtils.displayImage(getContext(), viewHolder.objectPhoto, PhotoUtils.getFullPhotoUrl(eventDto.getCheck().getTaskPhotoUrl()), PhotoUtils.convertDpToPixels(48, getContext()), -1, false);
        } else if (eventDto.getPhotoDto() != null && !TextUtils.isEmpty(eventDto.getPhotoDto().getUrl())) {
            viewHolder.objectPhoto.setVisibility(View.VISIBLE);
            viewHolder.objectPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(PhotoActivity.buildIntent(getContext(), eventDto.getPhotoDto().getId()));
                }
            });
            PhotoUtils.displayImage(getContext(), viewHolder.objectPhoto, PhotoUtils.getFullPhotoUrl(eventDto.getPhotoDto().getUrl()), PhotoUtils.convertDpToPixels(48, getContext()), -1, false);
        } else if (eventDto.getObjectUser() != null) {
            viewHolder.objectPhoto.setVisibility(View.VISIBLE);
            viewHolder.objectPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(ProfileActivity.buildIntent(getContext(), eventDto.getObjectUser().getId()));
                }
            });
            if (!TextUtils.isEmpty(eventDto.getObjectUser().getAvatar())) {
                PhotoUtils.displayImage(getContext(), viewHolder.objectPhoto, LashGoUtils.getUserAvatarUrl(eventDto.getObjectUser().getAvatar()), PhotoUtils.convertDpToPixels(40, getContext()), R.drawable.ava, false);
            } else {
                viewHolder.objectPhoto.setImageResource(R.drawable.ava);
            }
        } else {
            viewHolder.objectPhoto.setVisibility(View.GONE);
        }

        viewHolder.eventDate.setText(simpleDateFormat.format(eventDto.getEventDate()));
        viewHolder.actionText.setText(buildEventText(eventDto));
        return convertView;
    }

    private SpannableStringBuilder buildEventText(final EventDto eventDto) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (eventDto.getUser() != null) {
            if (!TextUtils.isEmpty(eventDto.getUser().getFio())) {
                spannableStringBuilder.append(eventDto.getUser().getFio());
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, eventDto.getUser().getFio().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (!TextUtils.isEmpty(eventDto.getUser().getLogin())) {
                spannableStringBuilder.append(eventDto.getUser().getLogin());
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, eventDto.getUser().getLogin().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (eventDto.getAction().equals(DbCodes.EventActions.SUBSCRIBE.name())) {
            spannableStringBuilder.append(" " + getContext().getString(R.string.signed_up) + " ");
        } else if (eventDto.getAction().equals(DbCodes.EventActions.COMMENT.name())) {
            spannableStringBuilder.append(" " + getContext().getString(R.string.commented) + " ");
        } else if (eventDto.getAction().equals(DbCodes.EventActions.WIN.name())) {
            if (eventDto.getUser() == null) {
                spannableStringBuilder.append(getContext().getString(R.string.you));
            }
            spannableStringBuilder.append(" " + getContext().getString(R.string.won) + " ");
        }
        if (eventDto.getObjectUser() != null) {
            int start = spannableStringBuilder.length();
            if (!TextUtils.isEmpty(eventDto.getObjectUser().getFio())) {
                spannableStringBuilder.append(eventDto.getObjectUser().getFio());
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, start + eventDto.getObjectUser().getFio().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (!TextUtils.isEmpty(eventDto.getObjectUser().getLogin())) {
                spannableStringBuilder.append(eventDto.getObjectUser().getLogin());
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, start + eventDto.getObjectUser().getLogin().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableStringBuilder;
    }

}
