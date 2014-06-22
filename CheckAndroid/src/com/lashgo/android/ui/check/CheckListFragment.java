package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lashgo.android.R;

/**
 * Created by Eugene on 19.06.2014.
 */
public class CheckListFragment extends Fragment {

    public CheckListFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        ((ImageView) rootView.findViewById(R.id.image)).setImageResource(R.drawable.saturn);
        getActivity().setTitle(R.string.check_list);
        return rootView;
    }

    public static Fragment newInstance() {
        return new CheckListFragment();
    }
}
