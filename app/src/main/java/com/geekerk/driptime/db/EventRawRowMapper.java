package com.geekerk.driptime.db;

import android.text.TextUtils;

import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.dao.RawRowMapper;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by s21v on 2016/5/30.
 */
public class EventRawRowMapper implements RawRowMapper<EventBean> {
    @Override
    public EventBean mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        EventBean eventBean = new EventBean();
        ListBean listBean = null;
        UserBean userBean = null;
        for (int i = 0; i < columnNames.length; i++) {
            String column = columnNames[i];
            if ("id".equals(column))
                eventBean.setId(Integer.parseInt(resultColumns[i]));
            else if ("title".equals(column))
                eventBean.setTitle(resultColumns[i]);
            else if ("deadline".equals(column)) {
                if (!TextUtils.isEmpty(resultColumns[i]))
                    try {
                        eventBean.setDeadline(simpleDateFormat.parse(resultColumns[i]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
            } else if ("release_time".equals(column)) {
                try {
                    eventBean.setReleaseTime(simpleDateFormat.parse(resultColumns[i]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if ("isFinished".equals(column)) {
                eventBean.setFinished(resultColumns[i].equals("1") ? true : false);
            } else if ("priorityLevel".equals(column)) {
                eventBean.setPriorityLevel(Integer.parseInt(resultColumns[i]));
            } else if ("userId".equals(column)) {
                userBean = new UserBean(Integer.parseInt(resultColumns[i]));
                eventBean.setUser(userBean);
            } else if ("listId".equals(column)) {
                if (resultColumns[i] != null) {
                    listBean = new ListBean(Integer.parseInt(resultColumns[i]));
                }
            }
        }
        if (listBean != null) {
            listBean.setUser(userBean);
            eventBean.setList(listBean);
        }
        return eventBean;
    }
}
