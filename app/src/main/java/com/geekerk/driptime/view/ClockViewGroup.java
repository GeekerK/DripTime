package com.geekerk.driptime.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.geekerk.driptime.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/21.
 */
public class ClockViewGroup extends RelativeLayout {
    private SimpleDateFormat format;
    private View hourIv, minuteIv, secondIv;

    public ClockViewGroup(Context context) {
        this(context, null, 0);
    }

    public ClockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        format = new SimpleDateFormat("h:m:s");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hourIv = getChildAt(1);
        minuteIv = getChildAt(0);
        secondIv = getChildAt(2);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        super.dispatchDraw(canvas);
    }

    public void initClock() {
        String[] date = format.format(new Date()).split(":");
        int hour = Integer.decode(date[0]);
        int minute = Integer.decode(date[1]);
        int second = Integer.decode(date[2]);

        //跟根据当前时间计算时分秒针转动的度数
        float hourDegrees = (float) ((hour-3)*30 + minute*0.5);
        float minuteDegrees = (float) ((minute-15)*6 + second*0.1);
        float secondDegrees = (second-15)*6;

        //动画
        ObjectAnimator hourAnimator = ObjectAnimator.ofFloat(hourIv, "rotation", hourDegrees);
        ObjectAnimator minuteAnimator = ObjectAnimator.ofFloat(minuteIv, "rotation", minuteDegrees);
        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(secondIv, "rotation", secondDegrees);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(hourAnimator, minuteAnimator, secondAnimator);
        animatorSet.start();
    }

    public void clockGo() {
        //时分秒针每秒转到的度数
        float hourDegrees = hourIv.getRotation()+0.008f;
        float minuteDegrees = minuteIv.getRotation()+0.1f;
        float secondDegrees = secondIv.getRotation()+6;

        //ObjectAnimator.ofFloat的最后一个参数是动画结束的位置，所以之前的计算要用偏移量加上之前的旋转角度
        ObjectAnimator hourAnimator = ObjectAnimator.ofFloat(hourIv, "rotation", hourDegrees);
        ObjectAnimator minuteAnimator = ObjectAnimator.ofFloat(minuteIv, "rotation", minuteDegrees);
        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(secondIv, "rotation", secondDegrees);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1);
        animatorSet.playTogether(hourAnimator, minuteAnimator, secondAnimator);
        animatorSet.start();
    }
}
