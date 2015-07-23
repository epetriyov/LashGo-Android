package com.lashgo.mobile.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.lashgo.mobile.ui.check.ExtendedTimerFinishedListener;
import com.lashgo.mobile.ui.check.TimerFinishedListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 06.09.2014.
 */
public final class UiUtils {

    private UiUtils() {

    }

    public static void updateCheckTime(long millisUntilFinished, TextView textView) {
        long remainingHours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long remainingMinutes;
        long remainingSeconds;
        if (remainingHours > 0) {
            remainingMinutes = (millisUntilFinished - remainingHours * DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
            remainingSeconds = (millisUntilFinished - remainingHours * DateUtils.HOUR_IN_MILLIS - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
            textView.setText(String.valueOf(remainingHours) + ":" + (remainingMinutes < 10 ? "0" : "") + String.valueOf(remainingMinutes) + ":" + (remainingSeconds < 10 ? "0" : "") + String.valueOf(remainingSeconds));
        } else {
            remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
            remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
            textView.setText(String.valueOf(remainingMinutes) + ":" + (remainingSeconds < 10 ? "0" : "") + String.valueOf(remainingSeconds));
        }
    }

    public static void startTimer(long finishMillis, final TextView textView, final TimerFinishedListener timerFinishedListener) {
        if (finishMillis < System.currentTimeMillis()) {
            if (timerFinishedListener != null) {
                timerFinishedListener.onTimerFinished();
            }
        }
        if (textView == null) {
            throw new IllegalArgumentException("Empty textView for startTimer method");
        }
        new CountDownTimer(finishMillis - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) {

            private boolean isFinished;

            @Override
            public void onTick(long millisUntilFinished) {
                long remainingHours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long remainingMinutes;
                long remainingSeconds;
                if (remainingHours > 0) {
                    remainingMinutes = (millisUntilFinished - remainingHours * DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
                    remainingSeconds = (millisUntilFinished - remainingHours * DateUtils.HOUR_IN_MILLIS - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
                    textView.setText(String.valueOf(remainingHours) + ":" + (remainingMinutes < 10 ? "0" : "") + String.valueOf(remainingMinutes) + ":" + (remainingSeconds < 10 ? "0" : "") + String.valueOf(remainingSeconds));
                } else {
                    remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    remainingSeconds = (millisUntilFinished - remainingMinutes * DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
                    textView.setText(String.valueOf(remainingMinutes) + ":" + (remainingSeconds < 10 ? "0" : "") + String.valueOf(remainingSeconds));
                }

            }

            @Override
            public void onFinish() {
                if (!isFinished && timerFinishedListener != null) {
                    isFinished = true;
                    timerFinishedListener.onTimerFinished();
                }
            }
        }.start();
    }


    public static void startTimer(final long finishMillis, final ExtendedTimerFinishedListener timerFinishedListener) {
        if (timerFinishedListener != null) {
            if (finishMillis <= System.currentTimeMillis()) {
                if (timerFinishedListener != null) {
                    timerFinishedListener.onTimerFinished();
                }
            }
            final long allMillis = finishMillis - System.currentTimeMillis();
            timerFinishedListener.onMinuteTick(allMillis);
            new CountDownTimer(allMillis, DateUtils.SECOND_IN_MILLIS) {

                private int minuteCounter = 0;

                private boolean isFinished;

                @Override
                public void onTick(long millisUntilFinished) {
                    timerFinishedListener.onSecondTick(millisUntilFinished);
                    minuteCounter++;
                    if (minuteCounter % TimeUnit.MINUTES.toSeconds(1) == 0) {
                        timerFinishedListener.onMinuteTick(millisUntilFinished);
                        minuteCounter = 0;
                    }
                }

                @Override
                public void onFinish() {
                    if (!isFinished) {
                        isFinished = true;
                        timerFinishedListener.onTimerFinished();
                    }
                }
            }.start();
        }
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
