package com.lashgo.android.ui.check;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.adapters.AdapterBinder;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 14.07.2014.
 */
public class CheckItemBinder extends AdapterBinder {

    public CheckItemBinder(Context context) {
        super(context);
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
        if (convertView == null) {
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
        CheckDto checkDto = (CheckDto) itemData;
        viewHolder.checkName.setText(checkDto.getName());
        viewHolder.checkDescription.setText(checkDto.getDescription());
        int imageSize = PhotoUtils.convertPixelsToDp(48, getContext());
        if (!TextUtils.isEmpty(checkDto.getPhotoUrl())) {
            Picasso.with(getContext()).load(PhotoUtils.getFullPhotoUrl(checkDto.getPhotoUrl())).centerInside().
                    resize(imageSize, imageSize).transform(new CircleTransformation()).into(viewHolder.checkIcon);
        }
        Calendar checkFinishCalendar = Calendar.getInstance();
        checkFinishCalendar.setTime(checkDto.getStartDate());
        checkFinishCalendar.add(Calendar.HOUR, checkDto.getDuration());
        Calendar checkVoteCalendar = Calendar.getInstance();
        checkVoteCalendar.setTime(checkDto.getStartDate());
        checkVoteCalendar.add(Calendar.HOUR, checkDto.getDuration() + checkDto.getVoteDuration());
        if (checkFinishCalendar.getTimeInMillis() - System.currentTimeMillis() > 0) {
            /**
             * check is active
             */
            viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
            new CheckCounter(checkFinishCalendar.getTimeInMillis() - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, viewHolder.checkRemainingTime).start();
        } else if (checkVoteCalendar.getTimeInMillis() - System.currentTimeMillis() > 0) {
            /**
             * vote is going
             */
            viewHolder.checkRemainingTime.setVisibility(View.VISIBLE);
            new CheckCounter(checkVoteCalendar.getTimeInMillis() - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, viewHolder.checkRemainingTime).start();
        } else {
            /**
             * check finished
             */
            viewHolder.checkRemainingTime.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class CheckCounter extends CountDownTimer {

        private TextView checkRemainingTime;

        public CheckCounter(long millisInFuture, long countDownInterval, TextView checkRemainingTime) {
            super(millisInFuture, countDownInterval);
            this.checkRemainingTime = checkRemainingTime;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
            long remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
            checkRemainingTime.setText(String.valueOf(remainingMinutes) + ":" + String.valueOf(remainingSeconds));
        }

        @Override
        public void onFinish() {

        }
    }
}
