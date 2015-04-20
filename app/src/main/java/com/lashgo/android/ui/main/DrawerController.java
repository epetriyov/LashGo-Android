package com.lashgo.android.ui.main;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.lashgo.android.R;
import com.lashgo.android.ui.actions.ActionListFragment;
import com.lashgo.android.ui.activity.ActivityFragment;
import com.lashgo.android.ui.check.CheckListFragment;
import com.lashgo.android.ui.views.DrawerItemView;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.MainScreenInfoDto;

/**
 * Created by Eugene on 18.04.2015.
 */
public class DrawerController implements View.OnClickListener {

    private ViewGroup rootView;

    private DrawerItemView itemActions;

    private DrawerItemView itemChecks;

    private DrawerItemView itemNews;

    private DrawerItemView itemSubscribes;

    private DrawerOnClickListener onClickListener;

    public DrawerController(ViewGroup rootView, DrawerOnClickListener onClickListener) {
        this.rootView = rootView;
        this.onClickListener = onClickListener;
    }

    public void init() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhotoUtils.convertDpToPixels(48, rootView.getContext()));
        itemActions = new DrawerItemView(rootView.getContext(), rootView.getContext().getString(R.string.actions_list), rootView.getContext().getResources().getDrawable(R.drawable.time));
        itemActions.setOnClickListener(this);
        rootView.addView(itemActions, params);
        itemChecks = new DrawerItemView(rootView.getContext(), rootView.getContext().getString(R.string.check_list), rootView.getContext().getResources().getDrawable(R.drawable.tasks));
        itemChecks.setOnClickListener(this);
        rootView.addView(itemChecks, params);
        itemNews = new DrawerItemView(rootView.getContext(), rootView.getContext().getString(R.string.news_list), rootView.getContext().getResources().getDrawable(R.drawable.news));
        itemNews.setOnClickListener(this);
        rootView.addView(itemNews, params);
        itemSubscribes = new DrawerItemView(rootView.getContext(), rootView.getContext().getString(R.string.subscribes_list), rootView.getContext().getResources().getDrawable(R.drawable.follow));
        itemSubscribes.setOnClickListener(this);
        rootView.addView(itemSubscribes, params);
    }

    public void updateCounters(MainScreenInfoDto mainScreenInfoDto) {
        itemChecks.setCounter(mainScreenInfoDto.getTasksCount());
        itemActions.setCounter(mainScreenInfoDto.getActionCount());
        itemNews.setCounter(mainScreenInfoDto.getNewsCount());
        itemSubscribes.setCounter(mainScreenInfoDto.getSubscribesCount());
    }

    private View getViewByPosition(int position) {
        View view;
        switch (position) {
            case 0:
                view = itemActions;
                break;
            case 1:
                view = itemChecks;
                break;
            case 2:
                view = itemNews;
                break;
            case 3:
                view = itemSubscribes;
                break;
            default:
                view = itemChecks;
                break;
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        int position = 0;
        if (view == itemActions) {
            fragment = ActionListFragment.newInstance();
            position = 0;
        } else if (view == itemChecks) {
            fragment = CheckListFragment.newInstance(CheckListFragment.StartOptions.LOAD_ON_START);
            position = 1;
        } else if (view == itemNews) {
            fragment = ActivityFragment.newInstance(itemSubscribes.getCount(), false);
            position = 2;
        } else if (view == itemSubscribes) {
            fragment = ActivityFragment.newInstance(itemSubscribes.getCount(), true);
            position = 3;
        }
        if (onClickListener != null) {
            onClickListener.onClick(position, fragment);
        }
    }

    public void selectItem(int position) {
        onClick(getViewByPosition(position));
    }

    public interface DrawerOnClickListener {
        void onClick(int position, Fragment fragment);
    }
}
