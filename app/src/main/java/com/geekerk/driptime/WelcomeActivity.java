package com.geekerk.driptime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.geekerk.driptime.view.ClockViewGroup;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by s21v on 2016/5/12.
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int CLOCK_GO = 1;
    private static final String TAG = "WelcomeActivity";
    private ScheduledThreadPoolExecutor executor;
    private Handler handler;
    private ClockViewGroup clockViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        clockViewGroup = (ClockViewGroup) findViewById(R.id.clock);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (CLOCK_GO == msg.what) {
                    clockGoAnimator();
                }
            }
        };
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(CLOCK_GO);
            }
        }, 0, 1, TimeUnit.SECONDS);
        initAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private void initAnimation() {
        clockViewGroup.initClock();
    }

    private void clockGoAnimator() {
        clockViewGroup.clockGo();
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                Intent intent1 = new Intent(this, RegisterActivity.class);
                //共享元素过度动画
                startActivity(intent1, ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Pair.create(findViewById(R.id.iv_logo), "share_logo"),
                        Pair.create(findViewById(R.id.bt_register), "bt_register")).toBundle());
                break;
            case R.id.bt_signin:
                Intent intent2 = new Intent(this, SigninActivity.class);
                //共享元素过度动画
                startActivity(intent2, ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Pair.create(findViewById(R.id.iv_logo), "share_logo"),
                        Pair.create(findViewById(R.id.bt_signin), "bt_signin")).toBundle());
                break;
            case R.id.tv_skip:
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                finish();
                break;
        }
    }
}
