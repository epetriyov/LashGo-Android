package com.lashgo.android.ui.check;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.lashgo.android.R;
import com.lashgo.android.ui.adapters.MultyTypeAdapter;

/**
 * Created by Eugene on 19.06.2014.
 */
public class CheckListFragment extends Fragment {

    private ListView checkListView;

    private MultyTypeAdapter multyTypeAdapter;

    public CheckListFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        checkListView = (ListView) inflater.inflate(R.layout.frag_check_list, container, false);
        multyTypeAdapter = new MultyTypeAdapter();
        multyTypeAdapter.addBinder(R.layout.adt_check_item,new CheckItemBinder());
        multyTypeAdapter.addBinder(R.layout.adt_check_state, new CheckStateBinder());
        getActivity().setTitle(R.string.check_list);

        return checkListView;
    }

    public static Fragment newInstance() {
        return new CheckListFragment();
    }
}
