package com.lashgo.mobile.ui.check;

/**
 * Created by Eugene on 11.05.2015.
 */
public interface ExtendedTimerFinishedListener extends TimerFinishedListener{

    void onSecondTick(long millisUntilFinished);

    void onMinuteTick(long millisUntilFinished);
}
