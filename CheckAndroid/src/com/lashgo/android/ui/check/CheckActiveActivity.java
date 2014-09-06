package com.lashgo.android.ui.check;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.auth.LoginActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.android.utils.PhotoUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckActiveActivity extends CheckBaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int CHECH_PHOTO_PADDINGS = 130;
    private static final String TASK_PHOTO_TAG = "task_photo";
    private String imgPath;
    private CheckPagerAdapter pagerAdapter;
    private int imageSize;
    private ViewPager viewPager;
    private ImageView cameraBtn;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ExtraNames.PHOTO_URL.name(), imgPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_active);
        initViews();
    }

    @Override
    protected void initCheckDto(Bundle savedInstanceState) {
        super.initCheckDto(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            imgPath = intent.getStringExtra(ExtraNames.PHOTO_URL.name());
        }
        if (imgPath == null && savedInstanceState != null) {
            imgPath = savedInstanceState.getString(ExtraNames.PHOTO_URL.name());
        }
    }

    private void initViews() {
        if (checkDto != null) {
            imageSize = PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS;
            cameraBtn = (ImageView) findViewById(R.id.btn_camera);
            cameraBtn.setOnClickListener(this);
            ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
//            viewPager = (ViewPager) findViewById(R.id.check_pager);
//            viewPager.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageSize));
//            viewPager.requestLayout();
//            viewPager.setPageMargin(-50);
//            viewPager.setClipChildren(true);
            PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);
            viewPager = mContainer.getViewPager();
            viewPager.setPageMargin(50);
            viewPager.setClipChildren(false);
            mContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageSize));
            mContainer.requestLayout();
            mContainer.setOnPageChangeListener(this);
            pagerAdapter = new CheckPagerAdapter();
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
            if (pagerAdapter.getCount() > 1) {
                viewPager.setCurrentItem(1);
            }
            initBottomPanel();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_camera) {
            if (settingsHelper.isLoggedIn()) {
                DialogFragment makePhotoFragment = new MakePhotoDialog();
                makePhotoFragment.show(getFragmentManager(), MakePhotoDialog.TAG);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        } else if (view.getId() == R.id.btn_send_photo) {
            if (!TextUtils.isEmpty(imgPath)) {
                serviceHelper.sendPhoto(checkDto.getId(), imgPath);
            } else {
                Toast.makeText(this, R.string.error_send_photo, Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.check_photo) {
            String photoUrl = view.getTag() != null && view.getTag().equals(TASK_PHOTO_TAG) ? checkDto.getTaskPhotoUrl() : checkDto.getUserPhoto();
            startActivity(CheckPhotoActivity.newIntent(this, photoUrl, checkDto));
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (BaseIntentHandler.ServiceActionNames.ACTION_SEND_PHOTO.name().equals(action) && resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            pagerAdapter.hideSendPhotoBtn();
            imgPath = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == MakePhotoDialog.PICK_IMAGE) {
                if (data != null) {
                    imgPath = PhotoUtils.getAbsolutePath(this, data.getData());
                    addMinePhoto();
                } else {
                    Toast.makeText(this, R.string.empty_image_was_chosen, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == MakePhotoDialog.CAPTURE_IMAGE) {
                addMinePhoto();
            }
        }
    }

    /**
     * check photo done
     */
    private void addMinePhoto() {
        cameraBtn.setImageResource(R.drawable.btn_pink_camera);
        pagerAdapter.photoAdded();
        viewPager.setCurrentItem(1);
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1 && !TextUtils.isEmpty(imgPath)) {
            pagerAdapter.showSendPhotoBtn();
        } else {
            pagerAdapter.hideSendPhotoBtn();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class CheckPagerAdapter extends PagerAdapter {

        public CheckPagerAdapter() {
            super();
            if ((!TextUtils.isEmpty(checkDto.getUserPhoto()) || !TextUtils.isEmpty(imgPath)) && !TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                pagesCount = 2;
                cameraBtn.setVisibility(View.GONE);
            } else {
                pagesCount = 1;
            }
        }

        private int pagesCount;

        private View btnSend;

        public void photoAdded() {
            pagesCount = 2;
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.adt_check_pager, null);
            ImageView checkImage = (ImageView) view.findViewById(R.id.check_photo);
            checkImage.setOnClickListener(CheckActiveActivity.this);
            View btnSend = view.findViewById(R.id.btn_send_photo);
            btnSend.setOnClickListener(CheckActiveActivity.this);
            container.addView(view);
            switch (position) {
                case 0:
                    checkImage.setTag(TASK_PHOTO_TAG);
                    btnSend.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                        Picasso.with(CheckActiveActivity.this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerCrop().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    }
                    break;
                case 1:
                    this.btnSend = btnSend;
                    if (!TextUtils.isEmpty(imgPath)) {
                        btnSend.setVisibility(View.VISIBLE);
                        Picasso.with(CheckActiveActivity.this).load(Uri.fromFile(new File(imgPath))).centerCrop().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    } else {
                        btnSend.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(checkDto.getUserPhoto())) {
                            Picasso.with(CheckActiveActivity.this).load(PhotoUtils.getFullPhotoUrl(checkDto.getUserPhoto())).centerCrop().
                                    resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                        }
                    }
                    break;
                default:
                    break;
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        public void showSendPhotoBtn() {
            if (btnSend != null) {
                btnSend.setVisibility(View.VISIBLE);
            }
        }

        public void hideSendPhotoBtn() {

            if (btnSend != null) {
                btnSend.setVisibility(View.GONE);
            }
        }
    }
}
