package com.geekerk.driptime.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2016/5/21.
 */
public class ClockViewGroup extends RelativeLayout {
    private Calendar calendar;
    private View hourIv, minuteIv, secondIv;
    private int curHour, curMin, curSec;    //当前时分秒

    public ClockViewGroup(Context context) {
        this(context, null, 0);
    }

    public ClockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        //利用canvas实现抗锯齿效果
        PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        super.dispatchDraw(canvas);
    }

    public void initClock() {
        calendar = GregorianCalendar.getInstance();
        curHour = calendar.get(Calendar.HOUR);
        curMin = calendar.get(Calendar.MINUTE);
        curSec = calendar.get(Calendar.SECOND);
        //跟根据当前时间计算时分秒针转动的度数
        float hourDegrees = (float) ((curHour - 3) * 30 + curMin * 0.5);
        float minuteDegrees = (float) ((curMin - 15) * 6);// + curSec * 0.1);
        float secondDegrees = (curSec - 15) * 6;

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
        calendar = GregorianCalendar.getInstance();
        int newHour = calendar.get(Calendar.HOUR);
        int newMin = calendar.get(Calendar.MINUTE);
        int newSec = calendar.get(Calendar.SECOND);

        //时分秒针当前转到的度数
        float hourDegrees = hourIv.getRotation();
        float minuteDegrees = minuteIv.getRotation();
        float secondDegrees = secondIv.getRotation();

        boolean flag = false;
        secondDegrees += ((newSec-curSec)+60)%60 * 6;
        curSec = newSec;
        if (newMin != curMin) {
            flag = true;
            minuteDegrees += ((newMin-curMin)+60)%60 * 6;
            curMin = newMin;
            hourDegrees += ((newMin-curMin)+60)%60 * 0.5;
        }
        if (newHour != curHour) {
            hourDegrees = (newHour - 3) * 30;
            curHour = newHour;
        }

        //ObjectAnimator.ofFloat的最后一个参数是动画结束的位置，所以之前的计算要用偏移量加上之前的旋转角度
        ObjectAnimator hourAnimator = ObjectAnimator.ofFloat(hourIv, "rotation", hourDegrees);
        ObjectAnimator minuteAnimator = ObjectAnimator.ofFloat(minuteIv, "rotation", minuteDegrees);
        ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(secondIv, "rotation", secondDegrees);

        AnimatorSet animatorSet = new AnimatorSet();
        if (flag)
            animatorSet.playTogether(hourAnimator, minuteAnimator, secondAnimator);
        else
            animatorSet.play(secondAnimator);
        animatorSet.start();
    }
}
