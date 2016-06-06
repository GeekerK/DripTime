package com.geekerk.driptime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/5/29.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "DripTime.db";
    private static final int DATABASE_VERSION = 2;
    private Dao<EventBean, Integer> EventDao;
    private Dao<ListBean, Integer> ListDao;

    public DataBaseHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DataBaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, EventBean.class);
            TableUtils.clearTable(connectionSource, ListBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, EventBean.class, true);
            TableUtils.dropTable(connectionSource, ListBean.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<EventBean, Integer> getEventDao() throws SQLException {
        if (EventDao == null)
            EventDao = getDao(EventBean.class);
        return EventDao;
    }

    public Dao<ListBean, Integer> getListDao() throws SQLException {
        if (ListDao == null)
            ListDao = getDao(ListBean.class);
        return ListDao;
    }
}
