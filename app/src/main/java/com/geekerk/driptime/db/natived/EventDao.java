package com.geekerk.driptime.db.natived;

import com.geekerk.driptime.vo.EventBean;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/17.
 */
public class EventDao {
    //新建一个事件
    public void create(EventBean eventBean) throws SQLException {
        String sql = "insert into event (eventName, releaseTime, priorityLevel, userId, listId, deadline, isFinish) values ("+
                "'"+eventBean.getTitle()+"'， "+eventBean.getReleaseTime().getTime()+", "+eventBean.getPriorityLevel()+", "+eventBean.getUser().getId();
        //清单允许为空
        if (eventBean.getList() != null)
            sql += ", "+eventBean.getList().getId();
        else
            sql += ", null";
        //deadline允许为空
        if (eventBean.getDeadline() != null)
            sql += ", "+eventBean.getDeadline().getTime();
        else
            sql += ", null";

        if (eventBean.isFinished())
            sql += ", 1)";
        else
            sql += ", 0)";
    }

    //查询指定用户在一段时间内的所有未放到垃圾箱的事件，按ID降序 (对应 MainActivity 中的 BASE_QUERY)
    public static ArrayList<EventBean> queryByTime (int userId, int dustbinListId, String start, String end) {
        String sql = "select * from event where userId = "+ userId +" and (listId <> "+dustbinListId+" or listId is null) and releaseTime between datetime('"+start+"') and datetime('"+end+"') order by eventId DESC";
        return null;
    }

    //查询指定用户的所有未放到垃圾箱的事件，按ID降序  (对应 MainActivity 中的 QUERY_ALL)
    public static ArrayList<EventBean> queryAll (int userId, int dustbinListId) {
        String sql = "select * from event where userId = "+userId+" and (listId <> "+dustbinListId+" or listId is null) order by eventId DESC";
        return null;
    }

    //查询指定用户未放到垃圾箱的所有已完成的事件     (对应 MainActivity 中的 QUERY_COMPLETED)
    public static ArrayList<EventBean> queryCompleted (int userId, int dustbinListId) {
        String sql = "select * from event where userId = "+userId+" and isFinished = 1 and (listId <> "+dustbinListId+" or listId is null) order by eventId DESC";
        return null;
    }

    //查询指定用户的指定清单中的事件       (对应 MainActivity 中的 QUERY_BY_LIST)
    public static ArrayList<EventBean> queryByList (int userId, int listId) {
        String sql = "select * from event where userId = "+userId+" and listId = "+listId+" order by eventId DESC";
        return null;
    }

    //更新事件
    public void update(EventBean eventBean) throws SQLException {
        String sql = "update event set eventName='"+eventBean.getTitle()+"'"+
                ", priorityLevel="+eventBean.getPriorityLevel() +
                ", releaseTime="+eventBean.getReleaseTime().getTime();
        //这两项允许为空
        if (eventBean.getList() != null)
            sql += ", listId="+eventBean.getList().getId();
        if (eventBean.getDeadline() != null)
            sql += ", deadline="+eventBean.getDeadline().getTime();

        if (eventBean.isFinished())
            sql += ", isFinish=1";
        else
            sql += ", isFinish=0";
        sql += " where eventId = "+eventBean.getId();
    }

    //删除指定事件
    public void delete(EventBean selected) throws SQLException {
        String sql = "delete from event where eventId=" + selected.getId();
    }

    //批量删除
    public void deleteSet(ArrayList<EventBean> list) throws SQLException {
        String arg = "(";
        for (int i=0; i<list.size(); i++) {
            if (i != list.size() -1)
                arg += list.get(i).getId()+",";
            else
                arg += list.get(i).getId();
        }
        arg += ")";
        String sql = "delete from event where eventId in "+arg;
    }

    //删除指定用户，指定清单下的事件
    public void deleteByUserIdAndListId(int userId, int listId) throws SQLException {
        String sql = "delete from event where userId=" + userId + " and listId="+listId;
    }
}
