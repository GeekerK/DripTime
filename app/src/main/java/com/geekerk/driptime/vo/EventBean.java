package com.geekerk.driptime.vo;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventBean {
    private String title;
    private String deadline;

    public EventBean(String title, String deadline) {
        this.title = title;
        this.deadline = deadline;
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
}
