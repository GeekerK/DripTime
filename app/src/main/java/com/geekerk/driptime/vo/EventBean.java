package com.geekerk.driptime.vo;

import android.util.Log;

import com.geekerk.driptime.R;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventBean {
    private static final String TAG = "EventBean";

    public enum Priority {
        FIRST_LEVEL(R.color.priority_first, 1),
        SECOND_LEVEL(R.color.priority_second, 2),
        THIRD_LEVEL(R.color.priority_third, 3);

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

    private String title;
    private String deadline;
    private Priority priority;

    public EventBean(String title, String deadline, int priorityLevel) {
        this.title = title;
        this.deadline = deadline;
        this.priority = Priority.getPriority(priorityLevel);
        Log.i(TAG, this.priority.toString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getProrityColor() {
        return priority.colorRes;
    }
}
