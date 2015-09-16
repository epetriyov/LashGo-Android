package com.lashgo.mobile.ui.check;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lashgo.mobile.LashgoConstants;
import com.lashgo.mobile.R;
import com.lashgo.mobile.adapters.AdapterBinder;
import com.lashgo.mobile.ui.views.GradientImageView;
import com.lashgo.mobile.utils.LashGoUtils;
import com.lashgo.mobile.utils.PhotoUtils;
import com.lashgo.mobile.utils.UiUtils;
import com.lashgo.model.dto.CheckDto;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 14.07.2014.
 */
public class CheckItemBinder extends AdapterBinder {

    public interface OnCheckStateChangeListener {
        void onCheckStateChanged();
    }

    private OnCheckStateChangeListener onCheckStateChangeListener;

    public CheckItemBinder(Context context, OnCheckStateChangeListener onCheckStateChangeListener) {
        super(context);
        this.onCheckStateChangeListener = onCheckStateChangeListener;

    }

    private static class ViewHolder {
        private TextView checkName;
        private TextView checkDescription;
        private ImageView checkIcon;
        private TextView checkRemainingTime;
        public GradientImageView checkGradient;
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
            viewHolder.checkGradient = (GradientImageView) convertView.findViewById(R.id.check_gradient);
            viewHolder.checkRemainingTime = (TextView) convertView.findViewById(R.id.check_remaining_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CheckDto checkDto = (CheckDto) itemData;
        viewHolder.checkName.setText(checkDto.getName());
        viewHolder.checkDescription.setText(checkDto.getDescription());
        int imageSize;
        if (checkDto.getWinnerPhotoDto() != null && !TextUtils.isEmpty(checkDto.getWinnerPhotoDto().getUrl())) {
            imageSize = PhotoUtils.convertDpToPixels(92, getContext());
            updateImageLayoutParams(viewHolder.checkIcon, imageSize);
            PhotoUtils.displayImage(getContext(), viewHolder.checkIcon, PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoDto().getUrl()), imageSize, R.drawable.ava, false);
        } else if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
            imageSize = PhotoUtils.convertDpToPixels(76, getContext());
            updateImageLayoutParams(viewHolder.checkIcon, imageSize);
            PhotoUtils.displayImage(getContext(), viewHolder.checkIcon, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
        } else {
            imageSize = PhotoUtils.convertDpToPixels(92, getContext());
            updateImageLayoutParams(viewHolder.checkIcon, imageSize);
            viewHolder.checkIcon.setImageResource(R.drawable.ava);
        }
        LashgoConstants.CheckState checkState = LashGoUtils.getCheckState(checkDto);
        ExtendedTimerFinishedListener timerFinishedListener = new ExtendedTimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                viewHolder.checkGradient.updateImage(LashGoUtils.getCheckState(checkDto), 1f);
                if (onCheckStateChangeListener != null) {
                    onCheckStateChangeListener.onCheckStateChanged();
                }
            }

            @Override
            public void onSecondTick(long millisUntilFinished) {
                UiUtils.updateCheckTime(millisUntilFinished, viewHolder.checkRemainingTime);
            }

            @Override
            public void onMinuteTick(long millisUntilFinished) {
                long duration = TimeUnit.HOURS.toMillis(checkDto.getDuration());
                viewHolder.checkGradient.updateImage(LashGoUtils.getCheckState(checkDto), ((float)(duration - millisUntilFinished)) / duration);
            }
        };
        switch (checkState) {
            case ACTIVE:
                Calendar checkActiveCalendar = Calendar.getInstance();
                checkActiveCalendar.setTime(checkDto.getStartDate());
                checkActiveCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration());
                viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
                UiUtils.startTimer(checkActiveCalendar.getTimeInMillis(), timerFinishedListener);
                break;
            case VOTE:
                viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
                Calendar checkVoteCalendar = Calendar.getInstance();
                checkVoteCalendar.setTime(checkDto.getStartDate());
                checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
                UiUtils.startTimer(checkVoteCalendar.getTimeInMillis(), timerFinishedListener);
                break;
            case FINISHED:
                viewHolder.checkRemainingTime.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return convertView;
    }

    private void updateImageLayoutParams(ImageView imageView, int imageSize) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageSize;
        layoutParams.width = imageSize;
        imageView.setLayoutParams(layoutParams);
    }

}
