package com.lashgo.android.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;

/**
 * Created by Eugene on 18.04.2015.
 */
public class DrawerItemView extends FrameLayout {

    private ImageView itemIcon;

    private TextView itemName;

    private View itemCounter;

    private ImageView itemCounterBg;

    private TextView itemCounterValue;
    private int count;

    public DrawerItemView(Context context, String itemName, Drawable itemIcon) {
        super(context);
        init(itemName, itemIcon);
    }

    private void init(String itemNameValue, Drawable itemIconValue) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_drawer_item, this, true);
        itemIcon = (ImageView) findViewById(R.id.item_icon);
        itemName = (TextView) findViewById(R.id.item_name);
        itemCounter = findViewById(R.id.item_counter);
        itemCounterBg = (ImageView) findViewById(R.id.item_counter_bg);
        itemCounterValue = (TextView) findViewById(R.id.item_counter_value);
        itemName.setText(itemNameValue);
        itemIcon.setImageDrawable(itemIconValue);
    }

    public void setCounter(int counterValue) {
        this.count = counterValue;
        itemCounterValue.setText(String.valueOf(counterValue));
        if (counterValue > 9) {
            itemCounterBg.setImageResource(R.drawable.ic_notification_big);
        } else if (counterValue > 0 && counterValue <= 9) {
            itemCounterBg.setImageResource(R.drawable.ic_notification_small);
        } else {
            itemCounter.setVisibility(View.GONE);
        }
    }

    public int getCount() {
        return count;
    }

}
