package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.geekerk.driptime.MainActivity;
import com.geekerk.driptime.R;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.EventRawRowMapper;
import com.geekerk.driptime.vo.EventBean;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 所有事件列表fragment的基类
 * Created by Administrator on 2016/6/9.
 */
public abstract class BaseEventListFragment extends Fragment {
    private static final String TAG = "BaseEventListFragment";
    protected DataBaseHelper mDatabaseHelper;
    protected String mQuery;
    protected String[] mQueryArgs;
    protected DrawerLayout mDrawer;
    protected String mToolbarTitle;
    protected TextView emptyView;

    public static <T extends BaseEventListFragment> T getInstance(Class<T> c, DrawerLayout drawerLayout, String query, String... queryArgs) throws IllegalAccessException, java.lang.InstantiationException {
        T fragment = c.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        bundle.putStringArray("queryArgs", queryArgs);
        fragment.setArguments(bundle);
        fragment.mDrawer = drawerLayout;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabaseHelper = ((MainActivity) getActivity()).getDataBaseHelper();
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("query");
            mQueryArgs = savedInstanceState.getStringArray("queryArgs");
            mToolbarTitle = savedInstanceState.getString("toolbarTitle");
        } else {
            mQuery = getArguments().getString("query");
            mQueryArgs = getArguments().getStringArray("queryArgs");
        }
        setRetainInstance(true);
        queryLocalDatabase();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", mQuery);
        outState.putStringArray("query_args", mQueryArgs);
        outState.putSerializable("toolbarTitle", mToolbarTitle);
    }

    //从本地数据库获得数据
    protected ArrayList<EventBean> queryLocalDatabase() {
        ArrayList<EventBean> data = new ArrayList<>();
        try {
            EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
            GenericRawResults<EventBean> results = eventDao.queryRaw(mQuery, new EventRawRowMapper(), mQueryArgs);
            data.addAll(results.getResults());
//            // ----------- 测试 json --------------
//            Log.i(TAG, JsonUtil.getEventBeanArrayJson(data));
//            // ---------- end --------------
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setToolbarTitle(String title) {
        mToolbarTitle = title;
    }

    protected void initToolBar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (!TextUtils.isEmpty(mToolbarTitle))
            toolbar.setTitle(mToolbarTitle);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public abstract void resetData();

}
