package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckBaseActivity extends BaseActivity {

    private TO to;

    public static enum TO {VOTE, to, FINISHED}

    protected CheckDto checkDto;

    private boolean isResumed;

    protected CheckBottomPanelController bottomPanelController;
    private boolean timerFinished;

    public static Intent buildIntent(Context context, CheckDto checkDto, Class<? extends CheckBaseActivity> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_COUNTERS.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_COUNTERS.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (data != null) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_LIKE_CHECK.name().equals(action) && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                Boolean isLikeAdded = data.getBoolean(BaseIntentHandler.ServiceExtraNames.IS_LIKE_ADDED.name());
                if (isLikeAdded != null) {
                    bottomPanelController.updateLikesCount(isLikeAdded.booleanValue());
                }
            } else if ((BaseIntentHandler.ServiceActionNames.ACTION_GET_CHECK_COUNTERS.name().equals(action) || BaseIntentHandler.ServiceActionNames.ACTION_GET_PHOTO_COUNTERS.name().equals(action)) && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                bottomPanelController.udpateCounters((com.lashgo.model.dto.CheckCounters) data.getSerializable(BaseIntentHandler.ServiceExtraNames.COUNTERS.name()));
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
        if (checkDto == null) {
            throw new IllegalStateException("Check can't be null");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheckDto(savedInstanceState);
    }


    public void initBottomPanel() {
        bottomPanelController = new CheckBottomPanelController(CheckBottomPanelController.FROM.CHECK, this, checkDto);
    }

    public void setBottomPanel(CheckBottomPanelController checkBottomPanelController) {
        this.bottomPanelController = checkBottomPanelController;
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceHelper.getCheckCounters(checkDto.getId());
        isResumed = true;
        if (timerFinished) {
            onTimerFinished(to);
            timerFinished = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    public void onTimerFinished(TO to) {
        this.to = to;
        if (isResumed) {
            finish();
            if (to.equals(TO.VOTE)) {
                startActivity(CheckVoteActivity.buildIntent(this,
                        checkDto, CheckVoteActivity.class));
            } else {
                startActivity(CheckFinishedActivity.buildIntent(this,
                        checkDto, CheckFinishedActivity.class));
            }

        } else {
            timerFinished = true;
        }
    }
}
