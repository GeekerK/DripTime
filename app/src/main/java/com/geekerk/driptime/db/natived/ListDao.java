package com.geekerk.driptime.db.natived;

import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import java.sql.SQLException;
import java.util.List;

/**
 * 本地化操作
 * Created by s21v on 2016/6/17.
 */
public class ListDao {
    //每次新用户成功注册以后,建立跟这个用户相关的清单 如垃圾箱，收集箱（注意：垃圾箱在收集箱之前）
    public static void initUserList(UserBean userBean) {
        create(userBean.getId(), "垃圾桶");
        create(userBean.getId(), "收集箱");
    }

    //根据用户Id， 清单名称来创建清单，初始一律是未关闭状态
    public static ListBean create (int userId, String listName) {
        String sql = "INSERT INTO list (isClosed, listName, userId) values (0, "+listName+", "+userId+")";
        //执行查询
        //解析json，返回结果
        return null;
    }

    //通过userID,清单名称查找对应的清单,这里清单应该是唯一的（即同一用户下的清单不应该有重名的情况）
    public static ListBean queryByUserIdAndListname(int userId, String listName) {
        String sql = "select * from list where userId="+userId+" and listName='"+listName+"'";
        //执行查询
        //解析json，返回结果
        return null;
    }

    //得到用户除垃圾箱外所有的未关闭清单。 isClosed : 0 false , 1 true
    public static List<ListBean> queryByUserId(int userId, int isClosed) throws SQLException {
        String sql = "select * from list where userId=" + userId + " and isClosed=" + isClosed + " and listName!='垃圾桶'";
        //执行查询
        //解析json，返回结果
        return null;
    }

    //返回用户的自定义的清单
    public static List<ListBean> queryCustomList(int userId, boolean isClosed) throws SQLException {
        String sql = "select * from list where userId=" + userId + " and isClosed="+isClosed +" and listName!='垃圾桶' and listName!='收集箱'";

        return null;
    }

    //更新指定清单的名字
    public static void update(ListBean currentList, String newListName) throws SQLException {
        String sql = "update list set listName='" + newListName +"' where listId=" + currentList.getId() + "and userId=" + currentList.getUser().getId();
    }

    //删除清单
    public static void delete(ListBean mCurrentList) throws SQLException {
        String sql = "delete from list where listId="+mCurrentList.getId();
    }
}
