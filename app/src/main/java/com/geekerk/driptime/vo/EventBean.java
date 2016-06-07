package com.geekerk.driptime.vo;

import com.geekerk.driptime.R;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by s21v on 2016/5/26.
 */
@DatabaseTable(tableName = "table_event")
public class EventBean {
    private static final String TAG = "EventBean";
    @DatabaseField(columnName = "id", generatedId = true, dataType = DataType.INTEGER)
    private int id;
    @DatabaseField(columnName = "title", dataType = DataType.STRING)
    private String title;
    @DatabaseField(columnName = "deadline")
    private Date deadline;
    @DatabaseField(columnName = "release_time", dataType = DataType.DATE)
    private Date releaseTime;
    @DatabaseField(columnName = "priorityLevel", dataType = DataType.INTEGER)
    private int priorityLevel;
    @DatabaseField(columnName = "isFinished", dataType = DataType.BOOLEAN)
    private boolean isFinished;
    @DatabaseField(foreign = true, columnName = "listId", canBeNull = true)
    private ListBean list;  //清单，是一个外键
    @DatabaseField(foreign = true, columnName = "userId", canBeNull = false)
    private UserBean user;  //用户，外键且不能为空
    private Priority priority;

    public EventBean() {}

    public EventBean(int id, String title, Date deadline, Date releaseTime, int priorityLevel, boolean isFinished, UserBean user) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.releaseTime = releaseTime;
        this.priorityLevel = priorityLevel;
        this.isFinished = isFinished;
        this.user = user;
        for (Priority p : Priority.values()) {
            if (p.level == priorityLevel)
                priority = p;
        }
    }

    public EventBean(String title, Date deadline, Date releaseTime, int priorityLevel, boolean isFinished, UserBean user) {
        this(-1, title, deadline, releaseTime, priorityLevel, isFinished, user);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
        for (Priority p : Priority.values()) {
            if (p.level == priorityLevel)
                priority = p;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
        if (isFinished)
            setPriorityLevel(3);  //已完成
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProrityColorRes() {
        return priority.colorRes;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", deadline=" + deadline +
                ", releaseTime=" + releaseTime +
                ", priorityLevel=" + priorityLevel +
                ", isFinished=" + isFinished +
                ", list=" + list +
                ", user=" + user +
                ", priority=" + priority +
                '}';
    }

    public enum Priority {
        FIRST_LEVEL(R.color.priority_first, 1), //非常紧急
        SECOND_LEVEL(R.color.priority_second, 2),   //紧急
        THIRD_LEVEL(R.color.priority_third, 3), //已完成
        NORMAL_LEVEL(android.R.color.white, 0);

        private int level;
        private int colorRes;

        Priority(int colorRes, int level) {
            this.level = level;
            this.colorRes = colorRes;
        }

        public static Priority getPriority(int level) {
            for (Priority priority : Priority.values()) {
                if (priority.level == level)
                    return priority;
            }
            return null;
        }
    }
}
