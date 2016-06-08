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
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.db.UserDao;
import com.geekerk.driptime.utils.SecureUtil;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.concurrent.Callable;

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
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
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
        if (waveAnimatorSet.isStarted()) {
            waveAnimatorSet.end();
        }
    }

    public void doClick(View view) {
        if (view.getId() == R.id.bt_register) {
            if (TextUtils.isEmpty(usernameEt.getText())) {  //检查用户名是否为空
                Toast.makeText(this, "UserName " + getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(passwordEt.getText())) {   //检查密码是否为空
                Toast.makeText(this, "Password " + getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(emailEt.getText())) {   //检查邮箱是否为空
                Toast.makeText(this, "Email " + getResources().getString(R.string.inputIsEmpty), Toast.LENGTH_SHORT).show();
                return;
            } else {
                DataBaseHelper helper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
                try {
                    final UserDao userDao = new UserDao(helper.getUserDao());
                    final UserBean userBean = new UserBean(usernameEt.getText().toString(),
                            emailEt.getText().toString(),
                            SecureUtil.encodeBase64(passwordEt.getText().toString()));  //密码加密
                    //事务操作：添加用户，初始化用户清单
                    final ListDao listDao = new ListDao(helper.getListDao());
                    int result = TransactionManager.callInTransaction(helper.getConnectionSource(), new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            //检查邮箱是否已被注册
                            if (userDao.queryByEmail(userBean.getEmail()) == null) {
                                int i = userDao.create(userBean);
                                listDao.initUserList(userBean);
                                return i;
                            } else
                                return -1;
                        }
                    });

                    //用户注册成功
                    if (result == 1) {
                        result = userDao.refresh(userBean);
                        if (result == 1) {
                            //将当前用户id写入sharePreference
                            SharedPreferences preference = getSharedPreferences("user", MODE_PRIVATE);
                            preference.edit().putInt("currentUserID", userBean.getId()).commit();
                            //成功注册后跳转到主页面
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("currentUser", userBean);
                            startActivity(intent);
                            finish();
                        }
                    } else if (result == -1) {
                        //输入的邮箱已被注册
                        Toast.makeText(this, R.string.emailRepeat, Toast.LENGTH_SHORT).show();
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
