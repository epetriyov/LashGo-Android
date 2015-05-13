package com.lashgo.mobile.ui.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lashgo.mobile.R;
import com.lashgo.mobile.ui.BaseFragment;

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
        View rootView = inflater.inflate(R.layout.frag_news_list, container, false);
        getActivity().setTitle(R.string.news_list);
        return rootView;
    }

    @Override
    public void refresh() {

    }

    public static Fragment newInstance() {
        return new NewsFragment();
    }
}
