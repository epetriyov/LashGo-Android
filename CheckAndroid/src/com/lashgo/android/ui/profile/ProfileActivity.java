package com.lashgo.android.ui.profile;

import android.os.Bundle;
import android.widget.GridView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;

/**
 * Created by Eugene on 10.08.2014.
 */
public class ProfileActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        GridView photosGallery = (GridView) findViewById(R.id.photos_galley);
        photosGallery.setAdapter(new PhotoGalleryAdapter(this));
    }
}
