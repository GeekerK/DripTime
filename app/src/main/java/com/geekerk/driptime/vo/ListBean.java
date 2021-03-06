package com.geekerk.driptime.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 清单
 * Created by s21v on 2016/6/6.
 */
@DatabaseTable(tableName = "table_list")
public class ListBean implements Serializable {
    private static final long serialVersionUID = 1918600627920945379L;

    @DatabaseField(generatedId = true, columnName = "listId")
    private int id;
    @DatabaseField(columnName = "listName")
    private String name;
    @DatabaseField(canBeNull = false, columnName = "userID", foreign = true)
    private UserBean user;  //外键
    @DatabaseField(columnName = "isClosed")
    private boolean isClosed;

    public ListBean() {
    }

    public ListBean(int id) {
        this.id = id;
    }

    public ListBean(String listName) {
        name = listName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    @Override
    public String toString() {
        return "ListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListBean listBean = (ListBean) o;
        return id == listBean.id;
    }
}
