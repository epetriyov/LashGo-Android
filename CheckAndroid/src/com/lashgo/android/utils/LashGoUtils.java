package com.lashgo.android.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import com.lashgo.android.LashgoConfig;
import com.lashgo.model.dto.CheckDto;

import java.util.Calendar;

/**
 * Created by Eugene on 19.08.2014.
 */
public final class LashGoUtils {

    private LashGoUtils() {

    }

    public static LashgoConfig.CheckState getCheckState(CheckDto checkDto) {
        Calendar checkActiveCalendar = Calendar.getInstance();
        Calendar checkVoteCalendar = Calendar.getInstance();
        checkActiveCalendar.setTime(checkDto.getStartDate());
        checkVoteCalendar.setTime(checkDto.getStartDate());
        checkActiveCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration());
        checkVoteCalendar.add(Calendar.HOUR_OF_DAY, checkDto.getDuration() + checkDto.getVoteDuration());
        if ((checkActiveCalendar.getTimeInMillis() > System.currentTimeMillis())) {
            return LashgoConfig.CheckState.ACTIVE;
        } else if (checkVoteCalendar.getTimeInMillis() > System.currentTimeMillis()) {
            return LashgoConfig.CheckState.VOTE;
        } else {
            return LashgoConfig.CheckState.FINISHED;
        }
    }

    public static String getUserAvatarUrl(String avatar) {
        if(!TextUtils.isEmpty(avatar)) {
            if (avatar.contains("http://") || avatar.contains("https://")) {
                return avatar;
            }
            return PhotoUtils.getFullPhotoUrl(avatar);
        }
        return avatar;
    }

    public static Fragment findFragmentByPosition(FragmentActivity activity, ViewPager viewPager, FragmentPagerAdapter pagerAdapter, int position) {
        if (viewPager == null) {
            throw new IllegalArgumentException("ViewPager can't be null");
        }
        if (pagerAdapter == null) {
            throw new IllegalArgumentException("PagerAdapter can't be null");
        }
        return activity.getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + viewPager.getId() + ":"
                        + pagerAdapter.getItemId(position));
    }
}
