package com.geekerk.driptime;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.fragment.ContentListFragment;
import com.geekerk.driptime.nav.NavAdapter;
import com.geekerk.driptime.utils.DateUtil;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.NavBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ExpandableListView mNavMenu;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private static final String BASE_QUERY =
            "select * from table_event where release_time between datetime(?) and datetime(?) order by id DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavMenu = (ExpandableListView) drawer.findViewById(R.id.nav_menu);
        final List<String> list = new ArrayList<>();
        list.add("Today");
        list.add("All");
        list.add("Nearly Seven Days");
        list.add("Collection Box");
        list.add("Completed");
        list.add("Dustbin");
        list.add("Add Item");
        list.add("Settings");
        ArrayList<List<String>> arrayList = new ArrayList<>();

        List<NavBean> navBeanList = new ArrayList<>();
        NavBean navBeanToday = new NavBean(R.mipmap.nav_today, "Today", 3);
        NavBean navBeanAll = new NavBean(R.mipmap.nav_all, "All", 15);
        NavBean navBeanNearlySevenDays = new NavBean(R.mipmap.nav_nearlysevendays, "Nearly Seven Days", 28);
        NavBean navBeanCollectionBox = new NavBean(R.mipmap.nav_collectionbox, "Collection Box", 0);
        NavBean navBeanCompleted = new NavBean(R.mipmap.nav_completed, "Completed", 0);
        NavBean navBeanDustbin = new NavBean(R.mipmap.nav_dustbin, "Dustbin", 0);
        NavBean navBeanAddItem = new NavBean(R.mipmap.nav_additem, "Add Item", 0);
        NavBean navBeanSettings = new NavBean(R.mipmap.nav_settings, "Settings", 0);
        navBeanList.add(navBeanToday);
        navBeanList.add(navBeanAll);
        navBeanList.add(navBeanNearlySevenDays);
        navBeanList.add(navBeanCollectionBox);
        navBeanList.add(navBeanCompleted);
        navBeanList.add(navBeanDustbin);
        navBeanList.add(navBeanAddItem);
        navBeanList.add(navBeanSettings);
        mNavMenu.setAdapter(new NavAdapter(this, navBeanList, arrayList));

        mNavMenu.setSelectedGroup(0);

        mNavMenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                mCollapsingToolbarLayout.setTitle(list.get(groupPosition));
                switch (groupPosition) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainer,new ContentListFragment())
                                .addToBackStack("first")
                                .commit();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        //初始化数据库
        initData();
        //加载fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer,
                        ContentListFragment.getInstance(BASE_QUERY, DateUtil.getQueryBetweenDay()))
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_edit) {

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onDestroy() {
        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
        super.onDestroy();
    }

    public DataBaseHelper getDataBaseHelper() {
        if (dataBaseHelper == null)
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        return dataBaseHelper;
    }

    //添加模拟数据
    private void initData() {
        try {
            Dao<EventBean, Integer> eventDao = dataBaseHelper.getEventDao();
            eventDao.executeRaw("delete from table_event");
            eventDao.create(new EventBean("Add a task with multiple attribute",null, new Date(), 1, false));
            eventDao.create(new EventBean("Set timezone in settings-Preference ",new Date(116,5,29,14,0,0), new Date(), 0, false));
            eventDao.create(new EventBean("完成演示的PPT文稿，梳理讲解脉络",null, new Date(), 2, false));
            eventDao.create(new EventBean("新建一个目标清单，并完成",null, new Date(), 0, false));
            eventDao.create(new EventBean("回复Rick的邮件",null, new Date(), 3, true));
            eventDao.create(new EventBean("查找资料",null, new Date(), 3, true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
