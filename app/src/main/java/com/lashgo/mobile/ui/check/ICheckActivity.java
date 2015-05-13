package com.lashgo.mobile.ui.check;

import android.widget.ImageView;

/**
 * Created by Eugene on 30.11.2014.
 */
public interface ICheckActivity {

    void onTimerFinished(CheckActivity.TO to);

    void loadExpandedImage(final int position, final ImageView imageView);
}
