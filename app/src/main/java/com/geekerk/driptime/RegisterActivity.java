package com.geekerk.driptime;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by s21v on 2016/6/6.
 */
public class RegisterActivity extends AppCompatActivity {
    ImageView waveIv;
    EditText usernameEt, emailEt, passwordEt;
    AnimatorSet waveAnimatorSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        waveIv = (ImageView) findViewById(R.id.wave);
        usernameEt = (EditText) findViewById(R.id.username_et);
        emailEt = (EditText) findViewById(R.id.email_et);
        passwordEt = (EditText) findViewById(R.id.password_et);

        ObjectAnimator waveFadeAnimator, waveScaleXAnimator, waveScaleYAnimator;

        waveFadeAnimator = ObjectAnimator.ofFloat(waveIv, "alpha", 0f, 1f).setDuration(1000);
        waveFadeAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveFadeAnimator.setRepeatCount(ValueAnimator.INFINITE);

        waveScaleXAnimator = ObjectAnimator.ofFloat(waveIv, "scaleX", 0.2f, 1f).setDuration(1000);
        waveScaleXAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveScaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        waveScaleYAnimator = ObjectAnimator.ofFloat(waveIv, "scaleY", 0.2f, 1f).setDuration(1000);
        waveScaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveScaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);

        waveAnimatorSet = new AnimatorSet();
        waveAnimatorSet.playTogether(waveFadeAnimator, waveScaleXAnimator, waveScaleYAnimator);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!waveAnimatorSet.isStarted()) {
            waveAnimatorSet.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(waveAnimatorSet.isStarted()) {
            waveAnimatorSet.end();
        }
    }
}
