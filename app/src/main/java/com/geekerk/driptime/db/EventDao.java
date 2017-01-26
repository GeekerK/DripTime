package com.geekerk.driptime.db;

import com.geekerk.driptime.vo.EventBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s21v on 2016/6/7.
 */
public class EventDao {
    private static final String TAG = "EventDao";
    Dao<EventBean, Integer> dao;

    public EventDao(Dao<EventBean, Integer> dao) {
        this.dao = dao;
    }

    public void create(EventBean eventBean) throws SQLException {
        dao.create(eventBean);
    }

    public GenericRawResults<EventBean> queryRaw(String query, EventRawRowMapper eventRawRowMapper, String[] queryArgs) throws SQLException {
        return dao.queryRaw(query, eventRawRowMapper, queryArgs);
    }

    public void update(EventBean eventBean) throws SQLException {
        dao.update(eventBean);
    }

    public void delete(EventBean selected) throws SQLException {
        dao.delete(selected);
    }

    public void deleteSet(ArrayList<EventBean> list) throws SQLException {
        dao.delete(list);
    }

    public void deleteByUserIdAndListId(int userId, int listId) throws SQLException {
        dao.deleteBuilder().where().eq("userId", userId).and().eq("listId", listId);
    }

    public List<EventBean> queryByUserIdAndListId(int userId, int listId) throws SQLException {
        return dao.queryBuilder().where().eq("userId", userId).and().eq("listId", listId).query();
    }
}
