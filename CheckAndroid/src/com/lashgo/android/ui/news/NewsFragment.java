package com.lashgo.android.ui.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseFragment;

/**
 * Created by Eugene on 19.06.2014.
 */
public class NewsFragment extends BaseFragment {

    public NewsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_check_list, container, false);
        getActivity().setTitle(R.string.news_list);
        return rootView;
    }

    public static Fragment newInstance() {
        return new NewsFragment();
    }
}
