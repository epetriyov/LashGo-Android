package com.lashgo.android.ui.check;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.BaseFragment;
import com.lashgo.android.ui.images.CircleTransformation;
import com.lashgo.android.ui.views.PagerContainer;
import com.lashgo.android.utils.PhotoUtils;
import com.lashgo.model.dto.CheckDto;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 28.08.2014.
 */
public class CheckFinishedFragment extends BaseFragment implements View.OnClickListener {

    private static final int CHECH_PHOTO_PADDINGS = 130;

    private static final String TASK_PHOTO_TAG = "task_photo";

    private CheckDto checkDto;

    private CheckPagerAdapter pagerAdapter;
    private int imageSize;
    private ViewPager viewPager;

    public static Fragment newInstance(CheckDto checkDto) {
        CheckFinishedFragment fragment = new CheckFinishedFragment();
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
        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.frag_finished_check, null);
        if (checkDto != null) {
            imageSize = PhotoUtils.getScreenWidth(getActivity()) - CHECH_PHOTO_PADDINGS;
            ((TextView) view.findViewById(R.id.check_name)).setText(checkDto.getName());
            ((TextView) view.findViewById(R.id.task_description)).setText(checkDto.getDescription());
            PagerContainer mContainer = (PagerContainer) view.findViewById(R.id.pager_container);
            viewPager = mContainer.getViewPager();
            viewPager.setPageMargin(50);
            viewPager.setClipChildren(false);
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageSize));
            mContainer.requestLayout();
            pagerAdapter = new CheckPagerAdapter();
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
            if (pagerAdapter.getCount() > 1) {
                viewPager.setCurrentItem(1);
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((CheckBaseActivity)getActivity()).initBottomPanel();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.check_photo) {
            if (view.getTag() != null && view.getTag().equals(TASK_PHOTO_TAG)) {
                startActivity(CheckPhotoActivity.newIntent(getActivity(), checkDto.getTaskPhotoUrl(), checkDto));
            } else {
                startActivity(CheckPhotoActivity.newIntent(getActivity(), checkDto.getWinnerPhotoUrl(), checkDto));
            }
        }
    }

    private class CheckPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public int getCount() {
            return checkDto.getWinnerInfo() != null ? 2 : 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.adt_check_finished_pager, null);
            View winnerMedal = view.findViewById(R.id.winner_medal);
            TextView winnerName = (TextView) view.findViewById(R.id.winner_name);
            ImageView checkImage = (ImageView) view.findViewById(R.id.check_photo);
            checkImage.setOnClickListener(CheckFinishedFragment.this);
            container.addView(view);
            switch (position) {
                case 0:
                    winnerMedal.setVisibility(View.GONE);
                    winnerName.setVisibility(View.GONE);
                    checkImage.setTag(TASK_PHOTO_TAG);
                    if (!TextUtils.isEmpty(checkDto.getTaskPhotoUrl())) {
                        Picasso.with(getActivity()).load(PhotoUtils.getFullPhotoUrl(checkDto.getTaskPhotoUrl())).centerCrop().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    }
                    break;
                case 1:
                    winnerMedal.setVisibility(View.VISIBLE);
                    winnerName.setVisibility(View.VISIBLE);
                    winnerName.setText(checkDto.getWinnerInfo().getLogin());
                    if (!TextUtils.isEmpty(checkDto.getWinnerPhotoUrl())) {
                        Picasso.with(getActivity()).load(PhotoUtils.getFullPhotoUrl(checkDto.getWinnerPhotoUrl())).centerCrop().
                                resize(imageSize, imageSize).transform(new CircleTransformation()).into(checkImage);
                    }
                    break;
                default:
                    break;
            }
            return view;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
