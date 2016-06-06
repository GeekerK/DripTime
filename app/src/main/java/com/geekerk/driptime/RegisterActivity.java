package com.geekerk.driptime;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.UserDao;
import com.geekerk.driptime.utils.SecureUtil;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.nio.charset.Charset;
import java.sql.SQLException;

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

        //跳转到登陆页面
        TextView login = (TextView) findViewById(R.id.switch_to_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

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

    //增加对于重复Email的检查
    //注册完成后初始化与该用户有关数据库清单（回收站和收集箱）
    public void doClick(View view) {
        if (view.getId() == R.id.bt_register) {
            if (TextUtils.isEmpty(usernameEt.getText())) {
                Toast.makeText(this, "UserName "+getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(emailEt.getText())) {
                Toast.makeText(this, "Email "+getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(passwordEt.getText())) {
                Toast.makeText(this, "Password "+getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else {
                DataBaseHelper helper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
                try {
                    UserDao userDao = new UserDao(helper.getUserDao());
                    userDao.create(usernameEt.getText().toString(), emailEt.getText().toString(), SecureUtil.encodeBase64(passwordEt.getText().toString()));
                    UserBean userBean = userDao.queryByEmail(emailEt.getText().toString());
                    String passwordSHA = SecureUtil.decodeBase64(userBean.getPassword());
                    if (passwordSHA.equals(new String(SecureUtil.SHA1(passwordEt.getText().toString()),Charset.forName("utf-8")))) {
                        Log.i("RegisterActivity", "equals!!!!!!!!!!!!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    OpenHelperManager.releaseHelper();
                    helper = null;
                }
            }
        }
    }
}
