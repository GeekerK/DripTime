package com.geekerk.driptime.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 清单
 * Created by s21v on 2016/6/6.
 */
@DatabaseTable(tableName = "table_list")
public class ListBean {
    @DatabaseField(generatedId = true, columnName = "listId")
    private int id;
    @DatabaseField(columnName = "listName")
    private String name;
    @DatabaseField(canBeNull = false, columnName = "userID", foreign = true)
    private UserBean user;  //外键

    public ListBean (){}

    public ListBean (String listName) {
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
}
