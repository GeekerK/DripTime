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
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.fragment.EventListWithCollapseToolBarFragment;
import com.geekerk.driptime.nav.NavAdapter;
import com.geekerk.driptime.utils.DateUtil;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.NavBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    //查询指定用户在一段时间内的所有未放到垃圾箱的事件，按ID降序
    private static final String BASE_QUERY =
            "select * from table_event where userId = ? and (listId <> ? or listId is null) and release_time between datetime(?) and datetime(?) order by id DESC";
    //查询指定用户的所有未放到垃圾箱的事件，按ID降序
    private static final String QUERY_ALL = "select * from table_event where userId = ? and (listId <> ? or listId is null) order by id DESC";
    //查询指定用户下指定清单的所有事件，按ID降序
    private static final String QUERY_IN_LIST = "select * from table_event where userId = ? and listId = ? order by id DESC";
    //查询指定用户未放到垃圾箱的所有已完成的事件
    private static final String QUERY_COMPLETED = "select * from table_event where userId = ? and isFinished = 1 and (listId <> ? or listId is null) order by id DESC";
    //当前用户Id，当前用户垃圾箱，收件箱ID
    private int userId, dustinListId, collectBoxListId;
    private ExpandableListView mNavMenu;
    private DataBaseHelper dataBaseHelper;
    private UserBean currentUser;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);

        if (savedInstanceState != null)
            currentUser = (UserBean) savedInstanceState.getSerializable("currentUser");
        else
            currentUser = (UserBean) getIntent().getSerializableExtra("currentUser");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                switch (groupPosition) {
                    case 0: //today
                        EventListWithCollapseToolBarFragment fragment = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        String[] args = DateUtil.getQueryBetweenDay();
                        if (fragment == null)
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]), "contentList")
                                    .commit();
                        else
                            fragment.changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
                        fragment.setToolBarTitle(list.get(groupPosition));
                        break;
                    case 1: //All
                        EventListWithCollapseToolBarFragment fragment1 = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        if (fragment1 == null)
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId)))
                                    .commit();
                        else
                            fragment1.changeData(QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId));
                        fragment1.setToolBarTitle(list.get(groupPosition));
                        break;
                    case 2: //week
                        EventListWithCollapseToolBarFragment fragment2 = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        String[] args1 = DateUtil.getQueryBetweenWeek();
                        if (fragment2 == null)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]), "contentList")
                                    .commit();
                        else
                            fragment2.changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]);
                        fragment2.setToolBarTitle(list.get(groupPosition));
                        break;
                    case 3: //Collection Box
                        EventListWithCollapseToolBarFragment fragment3 = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        if (fragment3 == null)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(QUERY_IN_LIST, String.valueOf(userId), String.valueOf(collectBoxListId)), "contentList")
                                    .commit();
                        else
                            fragment3.changeData(QUERY_IN_LIST, String.valueOf(userId), String.valueOf(collectBoxListId));
                        fragment3.setToolBarTitle(list.get(groupPosition));
                        break;
                    case 4: //Completed
                        EventListWithCollapseToolBarFragment fragment4 = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        if (fragment4 == null)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(QUERY_COMPLETED, String.valueOf(userId), String.valueOf(dustinListId)), "contentList")
                                    .commit();
                        else
                            fragment4.changeData(QUERY_COMPLETED, String.valueOf(userId), String.valueOf(dustinListId));
                        fragment4.setToolBarTitle(list.get(groupPosition));
                        break;
                    case 5: //Dustbin
                        EventListWithCollapseToolBarFragment fragment5 = (EventListWithCollapseToolBarFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        if (fragment5 == null)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, EventListWithCollapseToolBarFragment.getInstance(QUERY_IN_LIST, String.valueOf(userId), String.valueOf(dustinListId)), "contentList")
                                    .commit();
                        else
                            fragment5.changeData(QUERY_IN_LIST, String.valueOf(userId), String.valueOf(dustinListId));
                        fragment5.setToolBarTitle(list.get(groupPosition));
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        userId = currentUser.getId();
        dustinListId = 0;
        collectBoxListId = 0;
        try {
            ListDao listDao = new ListDao(dataBaseHelper.getListDao());
            dustinListId = listDao.queryByUserIdAndListname(userId, "垃圾桶").getId();
            collectBoxListId = listDao.queryByUserIdAndListname(userId, "收集箱").getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //初始化数据库
        initData();
        //加载fragment
        String[] arg = DateUtil.getQueryBetweenDay();
        EventListWithCollapseToolBarFragment fragment = EventListWithCollapseToolBarFragment.getInstance(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), arg[0], arg[1]);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment, "contentList")
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //快速新建按钮
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentUser", currentUser);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
            //------ 事件 ------
            EventDao eventDao = new EventDao(dataBaseHelper.getEventDao());
            eventDao.deleteAll();
            eventDao.create(new EventBean("测试测试4", null, new Date(116, 4, 26, 16, 0, 0), 0, false, currentUser));
            eventDao.create(new EventBean("测试测试3", null, new Date(116, 4, 27, 16, 0, 0), 0, false, currentUser));
            eventDao.create(new EventBean("测试测试2", null, new Date(116, 4, 28, 16, 0, 0), 0, false, currentUser));
            eventDao.create(new EventBean("测试测试1", null, new Date(116, 4, 29, 16, 0, 0), 0, false, currentUser));
            eventDao.create(new EventBean("Add a task with multiple attribute", null, new Date(), 1, false, currentUser));
            eventDao.create(new EventBean("Set timezone in settings-Preference ", new Date(116, 4, 29, 14, 0, 0), new Date(), 0, false, currentUser));
            eventDao.create(new EventBean("完成演示的PPT文稿，梳理讲解脉络", null, new Date(), 2, false, currentUser));
            eventDao.create(new EventBean("新建一个目标清单，并完成", null, new Date(), 0, false, currentUser));
            eventDao.create(new EventBean("回复Rick的邮件", null, new Date(), 3, true, currentUser));
            eventDao.create(new EventBean("查找资料", null, new Date(), 3, true, currentUser));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
