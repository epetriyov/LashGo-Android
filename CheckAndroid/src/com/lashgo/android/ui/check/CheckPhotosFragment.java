package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckPhotosFragment extends Fragment {
    public static Fragment newInstance(CheckDto checkDto) {
        CheckPhotosFragment fragment = new CheckPhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseActivity.ExtraNames.CHECK_DTO.name(),checkDto);
        fragment.setArguments(bundle);
        return fragment;
    }
}
