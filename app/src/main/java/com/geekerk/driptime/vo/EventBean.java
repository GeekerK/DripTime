package com.geekerk.driptime.vo;

import com.geekerk.driptime.R;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by s21v on 2016/5/26.
 */
@DatabaseTable(tableName = "table_event")
public class EventBean implements Serializable{
    private static final String TAG = "EventBean";
    private static final long serialVersionUID = 6405220463979523566L;

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

    public EventBean() {
    }

    public EventBean(int id, String title, Date deadline, Date releaseTime, int priorityLevel, boolean isFinished, UserBean user) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.releaseTime = releaseTime;
        this.priorityLevel = priorityLevel;
        this.isFinished = isFinished;
        this.user = user;
        this.priority = Priority.getPriority(this.priorityLevel);
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
        this.priority = Priority.getPriority(this.priorityLevel);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
//        if (isFinished)
//            setPriorityLevel(3);  //已完成
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

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        this.priorityLevel = priority.level;
    }

    public Priority getPriority() {
        return priority;
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
                '}';
    }

    public UserBean getUser() {
        return user;
    }

    public void setReleaseTime(long release_time) {
        setReleaseTime(new Date(release_time));
    }

    public void setDeadline(long deadline) {
        setDeadline(new Date(deadline));
    }

    public enum Priority {
        NORMAL_LEVEL(R.color.priority_normal, 0),    //普通
        SECOND_LEVEL(R.color.priority_second_level, 1),   //紧急
        FIRST_LEVEL(R.color.priority_first_level, 2); //非常紧急

//        THIRD_LEVEL(R.color.priority_third, 3), //已完成
//        NORMAL_LEVEL(android.R.color.white, 0);

        private int colorRes;
        private int level;

        Priority(int colorRes, int level) {
            this.level = level;
            this.colorRes = colorRes;
        }

        public static Priority getPriority(int level) {
            switch (level) {
                case 0:
                    return NORMAL_LEVEL;
                case 1:
                    return SECOND_LEVEL;
                case 2:
                    return FIRST_LEVEL;
                default:
                    return NORMAL_LEVEL;
            }
        }
    }
}
