package com.geekerk.driptime;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.UserDao;
import com.geekerk.driptime.utils.SecureUtil;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/5/23.
 */
public class SignInActivity extends AppCompatActivity {
    ImageView waveIv;
    AnimatorSet waveAnimatorSet;
    EditText emailEt, passwordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailEt = (EditText) findViewById(R.id.email_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        waveIv = (ImageView) findViewById(R.id.wave);

        //动画
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
        if (waveAnimatorSet.isStarted()) {
            waveAnimatorSet.end();
        }
    }

    public void doClick(View view) {
        //点击登陆按钮
        if (view.getId() == R.id.bt_signin) {
            if (TextUtils.isEmpty(emailEt.getText())) {
                Toast.makeText(this, "Email " + getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(passwordEt.getText())) {
                Toast.makeText(this, "Password " + getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else {
                DataBaseHelper helper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
                UserDao userDao = null;
                try {
                    userDao = new UserDao(helper.getUserDao());
                    UserBean userBean = userDao.queryByEmail(emailEt.getText().toString());
                    if (userBean == null) {
                        Toast.makeText(this, R.string.email_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String passwordSHA = SecureUtil.decodeBase64(userBean.getPassword());
                    if (passwordSHA.equals(new String(SecureUtil.SHA1(passwordEt.getText().toString()), Charset.forName("utf-8")))) {
                        //验证成功
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        sharedPreferences.edit().putInt("currentUserID", userBean.getId()).commit();
                        //跳转到主页面
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("currentUser", userBean);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    OpenHelperManager.releaseHelper();
                    helper = null;
                }
            }
        } else if (view.getId() == R.id.no_account_sign_up) {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
