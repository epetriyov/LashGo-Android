package com.lashgo.mobile.ui.actions;

import android.content.Context;
import android.content.Intent;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 30.05.2015.
 */
public class ActionWinnerActivity extends BaseActivity {

    public static Intent buildIntent(Context context, CheckDto checkDto)
    {
        Intent intent = new Intent(context,ActionWinnerActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(),checkDto);
        return intent;
    }

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }
}
