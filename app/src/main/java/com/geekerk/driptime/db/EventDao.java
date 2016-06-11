package com.geekerk.driptime.db;

import com.geekerk.driptime.vo.EventBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by s21v on 2016/6/7.
 */
public class EventDao {
    Dao<EventBean, Integer> dao;

    public EventDao(Dao<EventBean, Integer> dao) {
        this.dao = dao;
    }

    public void create(EventBean eventBean) throws SQLException {
        dao.create(eventBean);
    }

    public void deleteAll() throws SQLException {
        dao.deleteBuilder().delete();
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

    public void deleteCollection(ArrayList<EventBean> list) throws SQLException {
        dao.delete(list);
    }
}
