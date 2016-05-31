package com.geekerk.driptime.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by s21v on 2016/5/30.
 */
public class DateUtil {
    //间隔一天的查询条件
    public static String[] getQueryBetweenDay() {
        String[] result = new String[2];
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        result[0] = simpleDateFormat.format(now);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        result[1] = simpleDateFormat.format(calendar.getTime());
        return result;
    }

    //间隔一个星期的查询条件
    public static String[] getQueryBetweenWeek() {
        String[] result = new String[2];
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        result[0] = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 8);
        result[1] = simpleDateFormat.format(calendar.getTime());
        return result;
    }
}
