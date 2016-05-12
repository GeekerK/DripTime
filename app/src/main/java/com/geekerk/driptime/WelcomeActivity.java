package com.geekerk.driptime;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by s21v on 2016/5/12.
 */
public class WelcomeActivity extends AppCompatActivity {
    private ImageView hourIv, minuteIv, secondIv;
    private ScheduledThreadPoolExecutor executor;
    private Handler handler;
    private static final int CLOCK_GO = 1;
    private SimpleDateFormat format;
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        hourIv = (ImageView) findViewById(R.id.iv_hour);
        minuteIv = (ImageView) findViewById(R.id.iv_minute);
        secondIv = (ImageView) findViewById(R.id.iv_second);

        format = new SimpleDateFormat("h:m:s");
        executor = new ScheduledThreadPoolExecutor(1);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (CLOCK_GO == msg.what) {
                    clockGoAnimator();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAnimation();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(CLOCK_GO);
            }
        },0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        executor.shutdownNow();
    }

    private void initAnimation() {
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

    private void clockGoAnimator() {
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
