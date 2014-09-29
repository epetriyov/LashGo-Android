package com.lashgo.android.ui.check;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.R;
import com.lashgo.android.ui.adapters.AdapterBinder;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.LashGoUtils;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.android.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Created by Eugene on 14.07.2014.
 */
public class CheckItemBinder extends AdapterBinder {

    public  static interface OnCheckStateChangeListener
    {
        void onCheckStateChanged();
    }

    private OnCheckStateChangeListener onCheckStateChangeListener;

    public CheckItemBinder(Context context,OnCheckStateChangeListener onCheckStateChangeListener) {
        super(context);
        this.onCheckStateChangeListener = onCheckStateChangeListener;

    }

    private static class ViewHolder {
        private TextView checkName;
        private TextView checkDescription;
        private ImageView checkIcon;
        private TextView checkRemainingTime;
    }

    @Override
    public View bindData(View convertView, ViewGroup parent, Object itemData) {
        final ViewHolder viewHolder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            convertView = getInflater().inflate(R.layout.adt_check_item, null);
            viewHolder.checkName = (TextView) convertView.findViewById(R.id.check_name);
            viewHolder.checkDescription = (TextView) convertView.findViewById(R.id.check_description);
            viewHolder.checkIcon = (ImageView) convertView.findViewById(R.id.check_icon);
            viewHolder.checkRemainingTime = (TextView) convertView.findViewById(R.id.check_remaining_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CheckDto checkDto = (CheckDto) itemData;
        viewHolder.checkName.setText(checkDto.getName());
        viewHolder.checkDescription.setText(checkDto.getDescription());
        int imageSize = PhotoUtils.convertDpToPixels(48, getContext());
        if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            PhotoUtils.displayImage(getContext(), viewHolder.checkIcon, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
        }
        else
        {
            viewHolder.checkIcon.setImageResource(R.drawable.ava);
        }
        LashgoConfig.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        switch (checkState) {
            case ACTIVE:
                Calendar checkActiveCalendar = Calendar.getInstance();
                checkActiveCalendar.setTime(checkDto.getStartDate());
                checkActiveCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration());
                viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
                UiUtils.startTimer(checkActiveCalendar.getTimeInMillis(), viewHolder.checkRemainingTime, new TimerFinishedListener() {
                    @Override
                    public void onTimerFinished() {
                        if(onCheckStateChangeListener != null) {
                            onCheckStateChangeListener.onCheckStateChanged();
                        }
                    }
                });
                break;
            case VOTE:
                viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
                Calendar checkVoteCalendar = Calendar.getInstance();
                checkVoteCalendar.setTime(checkDto.getStartDate());
                checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
                UiUtils.startTimer(checkVoteCalendar.getTimeInMillis(), viewHolder.checkRemainingTime, new TimerFinishedListener() {
                    @Override
                    public void onTimerFinished() {
                        if(onCheckStateChangeListener != null) {
                            onCheckStateChangeListener.onCheckStateChanged();
                        }
                    }
                });
                break;
            case FINISHED:
                viewHolder.checkRemainingTime.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return convertView;
    }

}
