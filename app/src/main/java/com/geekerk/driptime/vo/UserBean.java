package com.geekerk.driptime.vo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 用户Bean
 * Created by s21v on 2016/6/6.
 */
@DatabaseTable(tableName = "table_user")
public class UserBean implements Serializable {
    private static final long serialVersionUID = -7017435001109959814L;

    @DatabaseField(generatedId = true, columnName = "userId")
    private int id;
    @DatabaseField(columnName = "userName")
    private String name;
    @DatabaseField(columnName = "userEmail")
    private String email;
    @DatabaseField(columnName = "password")
    private String password;

    public UserBean() {
    }

    public UserBean(int id) {
        this.id = id;
    }

    public UserBean(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
