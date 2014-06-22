package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckFragment extends Fragment {

    public static CheckFragment newInstance(CheckDto checkDto) {
        CheckFragment checkFragment = new CheckFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.KEY_CHECK_DTO, checkDto);
        checkFragment.setArguments(args);
        return checkFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
