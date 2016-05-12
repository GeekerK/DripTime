package com.geekerk.driptime.utils;

import android.content.Context;

/**
 * Created with Android Studio IDEA
 * Author: LiYan
 * Date: 2014/12/22
 * Time: 15:53
 */
public final class LayoutUtil {

    public static int getPixelByDIP(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public static int getDIPByPixel(Context context, int pixel) {
        return (int) (pixel
                / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int getPixelBySP(Context context, int sp) {
        return (int) (context.getResources().getDisplayMetrics().scaledDensity * sp);
    }

}
