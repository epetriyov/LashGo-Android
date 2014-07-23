package com.lashgo.android.ui.subscribes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lashgo.android.R;

/**
 * Created by Eugene on 19.06.2014.
 */
public class SubscribesFragment extends Fragment {

    public SubscribesFragment()
    {

    }

    public static SubscribesFragment newInstance()
    {
        return new SubscribesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_check_list, container, false);
        getActivity().setTitle(R.string.subscribes_list);
        return rootView;
    }

}
