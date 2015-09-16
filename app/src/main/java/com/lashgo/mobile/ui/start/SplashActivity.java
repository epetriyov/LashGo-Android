package com.lashgo.mobile.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lashgo.mobile.R;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.ui.auth.LoginActivity;
import com.lashgo.mobile.ui.main.MainActivity;
import com.lashgo.mobile.utils.UiUtils;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Eugene on 02.03.14.
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private static final int SPASH_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.act_splash);
        findViewById(R.id.root_layout).setPadding(0, UiUtils.getStatusBarHeight(this), 0, 0);
        ViewPager viewPager = (ViewPager) findViewById(R.id.splash_pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                int imageRes;
                if (position == 0) {
                    imageRes = R.drawable.photo_intro_1;
                } else if (position == 1) {
                    imageRes = R.drawable.photo_intro_2;
                } else {
                    imageRes = R.drawable.photo_intro_3;
                }
                ImageView imageView = new ImageView(SplashActivity.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Picasso.with(SplashActivity.this).load(imageRes).fit().centerCrop().into(imageView);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
        });
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        mIndicator.setViewPager(viewPager);
        findViewById(R.id.btn_skip).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void refresh() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_skip) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (v.getId() == R.id.btn_enter) {
            startActivityForResult(new Intent(this, LoginActivity.class), SPASH_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPASH_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }

}
