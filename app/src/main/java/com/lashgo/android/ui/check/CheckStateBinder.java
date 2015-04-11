package com.lashgo.android.ui.check;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.adapters.AdapterBinder;

/**
 * Created by Eugene on 14.07.2014.
 */
public class CheckStateBinder extends AdapterBinder {

    private static class ViewHolder {
        private TextView checkStatus;
    }

    public CheckStateBinder(Context context) {
        super(context);
    }

    @Override
    public View bindData(View convertView, ViewGroup parent, Object itemData) {
        ViewHolder viewHolder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            convertView = getInflater().inflate(R.layout.adt_check_state, null);
            viewHolder.checkStatus = (TextView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkStatus.setText((String) itemData);
        return convertView;
    }
}
