package com.geekerk.driptime.db.natived;

import com.geekerk.driptime.vo.UserBean;

/**
 * 本地化操作
 * Created by s21v on 2016/6/17.
 */
public class UserDao {
    //添加
    public static UserBean create(String userName, String userEmail, String userPwd) {
        //SQL语句
        String sql = "INSERT INTO user (userName, userEmail, userPwd) VALUES ('"+userName+"', '"+userEmail+"', '"+userPwd+"')";
        //调用本地方法传递SQL语句，链接数据库，获得返回的InputStream
        // if(msgCode == success)   成功创建后查询用户Id
              queryByEmail(userEmail);
        return null;
    }

    //按输入的Email值查找
    public static UserBean queryByEmail(String userEmail) {
        //SQL语句
        String sql = "select * from user where userEmail='"+userEmail+"'";
        //调用本地方法传递SQL语句，链接数据库，获得返回的InputStream
        return null;
    }

}
