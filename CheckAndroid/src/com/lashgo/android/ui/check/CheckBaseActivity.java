package com.lashgo.android.ui.check;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.model.dto.CheckCounters;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckBaseActivity extends BaseActivity {

    private TO to;

    public void initBottomPanel(View view) {
        bottomPanelController = new CheckBottomPanelController(CheckBottomPanelController.FROM.CHECK, this, view,checkDto);
        CheckCounters checkCounters = new CheckCounters();
        checkCounters.setPlayersCount(checkDto.getPlayersCount());
        bottomPanelController.udpateCounters(checkCounters);
    }

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
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name());
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (data != null) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_LIKE_PHOTO.name().equals(action) && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                Boolean isLikeAdded = data.getBoolean(BaseIntentHandler.ServiceExtraNames.IS_LIKE_ADDED.name());
                if (isLikeAdded != null) {
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
        if (savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initCheckDto(savedInstanceState);
    }


    public void initBottomPanel() {
        initBottomPanel(getWindow().getDecorView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkDto != null) {
            getCheckCounters();
        }
    }

    protected void getCheckCounters() {
        if (checkDto != null) {
            if (checkDto.getWinnerPhotoDto() != null) {
                serviceHelper.getPhotoCounters(checkDto.getWinnerPhotoDto().getId());
            } else {
                serviceHelper.getCheckCounters(checkDto.getId());
            }
        }
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

