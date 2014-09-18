package com.lashgo.android.ui.check;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.utils.PhotoUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Eugene on 10.09.2014.
 */
public class PhotoSentActivity extends BaseActivity implements View.OnClickListener {

    private String imgPath;

    public static Intent buildIntent(Context context, String imgPath) {
        Intent intent = new Intent(context, PhotoSentActivity.class);
        intent.putExtra(BaseActivity.ExtraNames.PHOTO_URL.name(), imgPath);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BaseActivity.ExtraNames.PHOTO_URL.name(), imgPath);
        super.onSaveInstanceState(outState);
    }

    private void initExtras(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            imgPath = intent.getStringExtra(BaseActivity.ExtraNames.PHOTO_URL.name());
        }
        if (savedInstanceState != null && imgPath == null) {
            imgPath = savedInstanceState.getString(BaseActivity.ExtraNames.PHOTO_URL.name());
        }
        if (imgPath == null) {
            throw new IllegalStateException("Image can't be empty");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        initExtras(savedInstanceState);
        setContentView(R.layout.act_sent_photo);
        findViewById(R.id.close_btn).setOnClickListener(this);
        Picasso.with(this).load(Uri.fromFile(new File(imgPath))).centerCrop().
                resize(PhotoUtils.getScreenWidth(this), PhotoUtils.convertDpToPixels(296, this)).into((android.widget.ImageView) findViewById(R.id.sent_photo));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close_btn) {
            finish();
        }
    }
}
