package com.lashgo.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AdapterBinder implements IAdapterBinder {

    private final LayoutInflater mInflater;

    public Context getContext() {
        return context;
    }

    private Context context;

    public AdapterBinder(Context context) {
        super();
        this.context = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public abstract View bindData(View convertView,
                                  ViewGroup parent, Object itemData);

    public LayoutInflater getInflater() {
        return this.mInflater;
    }

}
