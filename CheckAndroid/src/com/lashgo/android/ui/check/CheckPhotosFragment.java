package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.profile.PhotoGalleryAdapter;
import com.lashgo.android.ui.views.RobotoTextView;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.lashgo.model.dto.PhotoDto;

import java.util.ArrayList;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckPhotosFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private CheckDto checkDto;

    private ArrayList<PhotoDto> photoDtos;

    private GridView photosGallery;
    private PhotoGalleryAdapter photoGalleryAdapter;

    public static Fragment newInstance(CheckDto checkDto) {
        CheckPhotosFragment fragment = new CheckPhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseActivity.ExtraNames.CHECK_DTO.name(), checkDto);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            checkDto = (CheckDto) args.getSerializable(BaseActivity.ExtraNames.CHECK_DTO.name());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_check_results, container, false);
        if (checkDto != null) {
            int imageSize = PhotoUtils.convertDpToPixels(48, getActivity());
            view.findViewById(R.id.vote_time).setVisibility(View.GONE);
            TextView topText = ((RobotoTextView) view.findViewById(R.id.check_name));
            topText.setText(checkDto.getName());
            TextView checkDescription = ((RobotoTextView) view.findViewById(R.id.check_description));
            checkDescription.setText(checkDto.getDescription());
            ImageView taskPhoto = (ImageView) view.findViewById(R.id.task_photo);
            PhotoUtils.displayImage(getActivity(), taskPhoto, PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl()), imageSize, R.drawable.ava, false);
            photosGallery = (GridView) view.findViewById(R.id.photos_galley);
            int galImageSize = (PhotoUtils.getScreenWidth(getActivity()) - 24) / 3;
            photosGallery.setOnItemClickListener(this);
            photoGalleryAdapter = new PhotoGalleryAdapter(getActivity(), galImageSize);
            photosGallery.setAdapter(photoGalleryAdapter);
            if (photoDtos != null) {
                updateList();
            }
        }
        return view;
    }

    public void initGallery(final ArrayList<PhotoDto> photoDtos) {
        this.photoDtos = photoDtos;
        if (photoDtos != null && photoGalleryAdapter != null) {
            updateList();
        }
    }

    private void updateList() {
        photoGalleryAdapter.clear();
        for (PhotoDto photoDto : photoDtos) {
            photoGalleryAdapter.add(photoDto);
        }
        photoGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(PhotoActivity.buildIntent(getActivity(), photoDtos, i, ActivityReferrer.FROM_CHECK_GALLERY.name()));
    }
}
