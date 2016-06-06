package com.geekerk.driptime.db;

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

    public void create(String name, String email, String password) {
        UserBean userBean = new UserBean(name, email, password);
        try {
            userDao.create(userBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserBean queryByEmail(String email) {
        try {
            List<UserBean> resultList = userDao.queryForEq("userEmail", email);
            if (resultList!=null && resultList.size()==1)
                return resultList.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
