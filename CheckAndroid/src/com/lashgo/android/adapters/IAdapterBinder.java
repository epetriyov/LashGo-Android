package com.lashgo.android.adapters;

import android.view.View;
import android.view.ViewGroup;

public interface IAdapterBinder {
    View bindData(View convertView, ViewGroup parent, Object itemData);
}
