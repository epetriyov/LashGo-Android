package com.lashgo.android.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.lashgo.android.ui.check.TimerFinishedListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eugene on 06.09.2014.
 */
public final class UiUtils {

    private UiUtils() {

    }

    public static void startTimer(long finishMillis, final TextView textView, final TimerFinishedListener timerFinishedListener) {
        if (finishMillis < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Timer finish time can't be less than current time");
        }
        if (textView == null) {
            throw new IllegalArgumentException("Empty textView for startTimer method");
        }
        new CountDownTimer(finishMillis - System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) {
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
                if (timerFinishedListener != null) {
                    timerFinishedListener.onTimerFinished();
                }
            }
        }.start();
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
