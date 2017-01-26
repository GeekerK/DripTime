package com.geekerk.driptime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.db.UserDao;
import com.geekerk.driptime.view.ClockViewGroup;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by s21v on 2016/5/12.
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int CLOCK_GO = 1;
    private ScheduledThreadPoolExecutor mExecutor;
    private Handler mHandler;
    private ClockViewGroup mClockViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mClockViewGroup = (ClockViewGroup) findViewById(R.id.clock);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (CLOCK_GO == msg.what) {
                    clockGoAnimator();
                }
            }
        };
        mExecutor = new ScheduledThreadPoolExecutor(1);
        mExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(CLOCK_GO);
            }
        }, 0, 1, TimeUnit.SECONDS);
        initAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExecutor.shutdownNow();
    }

    private void initAnimation() {
        mClockViewGroup.initClock();
    }

    private void clockGoAnimator() {
        mClockViewGroup.clockGo();
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                //共享元素过度动画
                startActivity(intentRegister, ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Pair.create(findViewById(R.id.iv_logo), "share_logo"),
                        Pair.create(findViewById(R.id.bt_register), "bt_register")).toBundle());
                break;
            case R.id.bt_signin:
                Intent intentSignIn = new Intent(this, SignInActivity.class);
                //共享元素过度动画
                startActivity(intentSignIn, ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Pair.create(findViewById(R.id.iv_logo), "share_logo"),
                        Pair.create(findViewById(R.id.bt_signin), "bt_signin")).toBundle());
                break;
            case R.id.tv_skip:
                DataBaseHelper helper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
                try {
                    final UserDao userDao = new UserDao(helper.getUserDao());
                    final ListDao listDao = new ListDao(helper.getListDao());
                    UserBean result = TransactionManager.callInTransaction(helper.getConnectionSource(),
                            new Callable<UserBean>() {
                                @Override
                                public UserBean call() throws Exception {
                                    UserBean user = userDao.queryByEmail(Build.DEVICE);
                                    if (user == null) {
                                        user = new UserBean(Build.DEVICE, Build.DEVICE, Build.DEVICE);
                                        userDao.create(user);
                                        //初始化用户默认的清单
                                        listDao.initUserList(user);
                                        userDao.refresh(user);
                                    }
                                    return user;
                                }
                            });
                    //将当前用户id写入sharePreference
                    SharedPreferences preference = getSharedPreferences("user", MODE_PRIVATE);
                    preference.edit().putInt("currentUserID", result.getId()).commit();
                    //成功注册后跳转到主页面
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("currentUser", result);
                    startActivity(intent);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    OpenHelperManager.releaseHelper();
                    helper = null;
                }
                break;
            default:
                break;
        }
    }
}
