package com.geekerk.driptime.db;

import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * 清单DAO
 * Created by s21v on 2016/6/7.
 */
public class ListDao {
    Dao<ListBean, Integer> listDao;

    public ListDao(Dao<ListBean, Integer> listDao) {
        this.listDao = listDao;
    }

    //每次新用户成功注册以后,建立跟这个用户相关的清单 如垃圾箱，收集箱（注意：垃圾箱在收集箱之前）
    public void initUserList(UserBean userBean) throws SQLException {
        ListBean dustin = new ListBean("垃圾桶");
        dustin.setUser(userBean);
        ListBean collectionBox = new ListBean("收集箱");
        collectionBox.setUser(userBean);
        listDao.create(dustin);
        listDao.create(collectionBox);
//        // ------------- 测试json ------------
////        Log.i("ListDao", JsonUtil.getListBeanJson(dustin));
//        ArrayList<ListBean> lists = new ArrayList<>();
//        lists.add(dustin);
//        lists.add(collectionBox);
//        Log.i("ListDao", JsonUtil.getListBeanArrayJson(lists));
//        // -------------- end ---------------
    }

    //通过userID,清单名称查找对应的清单,这里清单应该是唯一的（即同一用户下的清单不应该有重名的情况）
    public ListBean queryByUserIdAndListname(int userId, String listname) throws SQLException {
        List<ListBean> results = listDao.queryBuilder().where().eq("userID", userId).and().eq("listName", listname).query();
        if (results != null && results.size() == 1)
            return results.get(0);
        return null;
    }

    //得到用户除垃圾箱外所有的未关闭清单
    public List<ListBean> queryByUserId(int userId, boolean isClosed) throws SQLException {
        return listDao.queryBuilder().where().eq("userID", userId).and().not().eq("listName", "垃圾桶")
                .and().eq("isClosed", isClosed).query();
    }

    //返回用户的自定义的清单
    public List<ListBean> queryCustomList(int userId, boolean isClosed) throws SQLException {
        return listDao.queryBuilder().where().eq("userID", userId).and().eq("isClosed", isClosed).and()
                .not().eq("listName", "垃圾桶").and().not().eq("listName", "收集箱").query();
    }

    public void create(ListBean listBean) throws SQLException {
        listDao.create(listBean);
    }

    public void update(ListBean currentList) throws SQLException {
        listDao.update(currentList);
    }

    public void delete(ListBean mCurrentList) throws SQLException {
        listDao.delete(mCurrentList);
    }
}
