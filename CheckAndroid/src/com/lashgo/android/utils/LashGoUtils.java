package com.lashgo.android.utils;

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
}
