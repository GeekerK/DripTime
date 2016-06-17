package com.geekerk.driptime.db;

import android.util.Log;

import com.geekerk.driptime.utils.JsonUtil;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6.
 */
public class UserDao {
    private Dao<UserBean, Integer> userDao;

    public UserDao(Dao<UserBean, Integer> userDao) {
        this.userDao = userDao;
    }

    public int create(UserBean userBean) throws SQLException {
        Log.i("UserDao", JsonUtil.getUserBeanJson(userBean)); //测试Json
        return userDao.create(userBean);
    }

    public UserBean queryByEmail(String email) throws SQLException {
        List<UserBean> resultList = userDao.queryForEq("userEmail", email);
        if (resultList != null && resultList.size() == 1)
            return resultList.get(0);
        return null;
    }

    public int refresh(UserBean userBean) throws SQLException {
        return userDao.refresh(userBean);
    }
}
