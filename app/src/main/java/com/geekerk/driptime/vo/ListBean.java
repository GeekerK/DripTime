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

    @Override
    public String toString() {
        return "ListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
