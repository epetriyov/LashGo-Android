package com.lashgo.android.ui.check;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 16.06.2014.
 */
public class CheckActiveActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int CHECH_PHOTO_PADDINGS = 130;
    private static final String TASK_PHOTO_TAG = "task_photo";
    private CheckDto checkDto;
    private String imgPath;
    private CheckPagerAdapter pagerAdapter;
    private int imageSize;
    private ViewPager viewPager;
    private CheckBottomPanelController bottomPanelController;

    public static Intent buildIntent(Context context, CheckDto checkDto) {
        Intent intent = new Intent(context, CheckActiveActivity.class);
        intent.putExtra(ExtraNames.CHECK_DTO.name(), checkDto);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.CHECK_DTO.name(), checkDto);
        super.onSaveInstanceState(outState);
    }

    private void initCheckDto(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            checkDto = (CheckDto) intent.getSerializableExtra(ExtraNames.CHECK_DTO.name());
        }
        if (checkDto == null && savedInstanceState != null) {
            checkDto = (CheckDto) savedInstanceState.getSerializable(ExtraNames.CHECK_DTO.name());
        }
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initCheckDto(savedInstanceState);
        initViews();
        bottomPanelController = new CheckBottomPanelController(this, checkDto);
    }

    private void initViews() {
        if (checkDto != null) {
            imageSize = PhotoUtils.convertPixelsToDp(PhotoUtils.getScreenWidth(this) - CHECH_PHOTO_PADDINGS, this);
            findViewById(R.id.btn_camera).setOnClickListener(this);
            ((TextView) findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) findViewById(R.id.task_description)).setText(checkDto.getDescription());
            viewPager = (ViewPager) findViewById(R.id.check_pager);
            viewPager.setPageMargin(15);
            viewPager.setClipChildren(true);
            pagerAdapter = new CheckPagerAdapter();
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOnPageChangeListener(this);
            if (pagerAdapter.getCount() > 1) {
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_camera) {
            DialogFragment makePhotoFragment = new MakePhotoDialog();
            makePhotoFragment.show(getFragmentManager(), MakePhotoDialog.TAG);
        } else if (view.getId() == R.id.btn_send_photo) {
            if (!TextUtils.isEmpty(imgPath)) {
                serviceHelper.sendPhoto(imgPath);
            } else {
                Toast.makeText(this, R.string.error_send_photo, Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.check_photo) {
            String photoUrl = view.getTag() != null && view.getTag().equals(TASK_PHOTO_TAG) ? checkDto.getTaskPhotoUrl() : checkDto.getPhotoUrl();
            startActivity(PhotoActivity.newIntent(this, photoUrl, checkDto));
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
            if (!TextUtils.isEmpty(checkDto.getPhotoUrl()) && !TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                pagesCount = 2;
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
        public int getCount() {
            return pagesCount;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.adt_check_pager, null);
            ImageView checkImage = (ImageView) view.findViewById(R.id.check_photo);
            checkImage.setOnClickListener(CheckActiveActivity.this);
            btnSend = view.findViewById(R.id.btn_send_photo);
            btnSend.setOnClickListener(CheckActiveActivity.this);
            container.addView(view);
            switch (position) {
                case 0:
                    checkImage.setTag(TASK_PHOTO_TAG);
                    btnSend.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                        Picasso.with(CheckActiveActivity.this).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerInside().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    }
                    break;
                case 1:
                    if (!TextUtils.isEmpty(imgPath)) {
                        btnSend.setVisibility(View.VISIBLE);
                        Picasso.with(CheckActiveActivity.this).load(imgPath).centerInside().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    } else {
                        btnSend.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(checkDto.getPhotoUrl())) {
                            Picasso.with(CheckActiveActivity.this).load(checkDto.getPhotoUrl()).centerInside().
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
            btnSend.setVisibility(View.VISIBLE);
        }

        public void hideSendPhotoBtn() {
            btnSend.setVisibility(View.GONE);
        }
    }
}
