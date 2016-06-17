package com.geekerk.driptime.db.natived;

import java.io.InputStream;

/**
 * 本地化操作
 * Created by s21v on 2016/6/17.
 */
public class ListDao {
    //根据用户Id， 清单名称来创建清单，初始一律是未关闭状态
    public InputStream create (int userId, String listName) {
        String sql = "INSERT INTO list (isClosed, listName, userId) values (0, "+listName+", "+userId+")";
        //执行查询
        return null;
    }

    //通过userID,清单名称查找对应的清单,这里清单应该是唯一的（即同一用户下的清单不应该有重名的情况）
    public InputStream queryByUserIdAndListname(int userId, String listName) {
        String sql = "select * from list where userId="+userId+" and listName="+listName;
        return null;
    }
}
