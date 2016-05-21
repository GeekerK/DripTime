package com.geekerk.driptime;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.geekerk.driptime.view.ClockViewGroup;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by s21v on 2016/5/12.
 */
public class WelcomeActivity extends AppCompatActivity {
    private ScheduledThreadPoolExecutor executor;
    private Handler handler;
    private static final int CLOCK_GO = 1;
    private static final String TAG = "WelcomeActivity";
    private ClockViewGroup clockViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        clockViewGroup = (ClockViewGroup) findViewById(R.id.clock);
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
            clockViewGroup.initClock();
    }

    private void clockGoAnimator() {
        clockViewGroup.clockGo();
    }
}
