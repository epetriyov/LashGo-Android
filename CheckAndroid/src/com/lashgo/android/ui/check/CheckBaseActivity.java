package com.lashgo.android.ui.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckBaseActivity extends BaseActivity {

    protected CheckDto checkDto;

    protected CheckBottomPanelController bottomPanelController;

    public static Intent buildIntent(Context context, CheckDto checkDto, Class<? extends CheckBaseActivity> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name().equals(action) && resultCode == BaseIntentHandler.SUCCESS_RESPONSE)
        {
            if(data != null) {
                Boolean isLikeAdded = data.getBoolean(BaseIntentHandler.ServiceExtraNames.IS_LIKE_ADDED.name());
                if(isLikeAdded != null) {
                    bottomPanelController.updateLikesCount(isLikeAdded.booleanValue());
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    protected void initCheckDto(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
        }
        if (checkDto == null && savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initCheckDto(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    public void initBottomPanel() {
        bottomPanelController = new CheckBottomPanelController(this, checkDto);
    }
}
