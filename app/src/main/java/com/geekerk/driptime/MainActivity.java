package com.geekerk.driptime;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.fragment.BaseEventListFragment;
import com.geekerk.driptime.fragment.DustbinFragment;
import com.geekerk.driptime.fragment.EventListWithCollapseToolBarFragment;
import com.geekerk.driptime.nav.NavAdapter;
import com.geekerk.driptime.utils.DateUtil;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.NavBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
    //查询指定用户的指定清单中的事件
    private static final String QUERY_BY_LIST = "select * from table_event where userId = ? and listId = ? order by id DESC";

    //当前用户Id，当前用户垃圾箱，收件箱ID
    private int userId, dustinListId, collectBoxListId;
    private ExpandableListView mNavMenu;
    private DataBaseHelper dataBaseHelper;
    private UserBean currentUser;
    private DrawerLayout drawer;
    private NavAdapter mNavAdapter;

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

        //初始化菜单数据
        final List<NavBean> navBeanList = new ArrayList<>();
        NavBean navBeanToday = new NavBean(R.mipmap.nav_today, "Today", 3);
        NavBean navBeanAll = new NavBean(R.mipmap.nav_all, "All", 15);
        NavBean navBeanNearlySevenDays = new NavBean(R.mipmap.nav_nearlysevendays, "Nearly Seven Days", 28);
        NavBean navBeanCollectionBox = new NavBean(R.mipmap.nav_collectionbox, "Collection Box", 0);
        NavBean navBeanCompleted = new NavBean(R.mipmap.nav_completed, "Completed", 0);
        NavBean navBeanDustbin = new NavBean(R.mipmap.nav_dustbin, "Dustbin", 0);
        NavBean navBeanAddItem = new NavBean(R.mipmap.nav_additem, "Lists", 0);
        NavBean navBeanSettings = new NavBean(R.mipmap.nav_settings, "Settings", 0);
        navBeanList.add(navBeanToday);
        navBeanList.add(navBeanAll);
        navBeanList.add(navBeanNearlySevenDays);
        navBeanList.add(navBeanCollectionBox);
        navBeanList.add(navBeanCompleted);
        navBeanList.add(navBeanDustbin);
        navBeanList.add(navBeanAddItem);
        navBeanList.add(navBeanSettings);
        mNavAdapter = new NavAdapter(this, navBeanList);
        mNavMenu.setAdapter(mNavAdapter);

        //设置 Group Click 监听
        mNavMenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                BaseEventListFragment fragment = (BaseEventListFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                try {
                    switch (groupPosition) {
                        case 0: //today
                            String[] args = DateUtil.getQueryBetweenDay();
                            if (fragment instanceof EventListWithCollapseToolBarFragment)
                            {
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
                            }
                            else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 1: //All
                            if (fragment instanceof EventListWithCollapseToolBarFragment)
                            {
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId));
                            }
                            else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 2: //week
                            String[] args1 = DateUtil.getQueryBetweenWeek();
                            if (fragment instanceof EventListWithCollapseToolBarFragment)
                            {
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]);
                            }
                            else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]);
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 3: //Collection Box
                            if (fragment instanceof EventListWithCollapseToolBarFragment)
                            {
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_IN_LIST, String.valueOf(userId), String.valueOf(collectBoxListId));
                            }
                            else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_IN_LIST, String.valueOf(userId), String.valueOf(collectBoxListId));
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 4: //Completed
                            if (fragment instanceof EventListWithCollapseToolBarFragment)
                            {
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_COMPLETED, String.valueOf(userId), String.valueOf(dustinListId));
                            }
                            else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_COMPLETED, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 5: //Dustbin
                            if (!(fragment instanceof DustbinFragment)) {
                                fragment = BaseEventListFragment.getInstance(DustbinFragment.class, drawer, QUERY_IN_LIST, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(navBeanList.get(groupPosition).getmNavName());
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 6: //Lists
                            if (mNavMenu.isGroupExpanded(groupPosition))
                                mNavMenu.collapseGroup(groupPosition);
                            else
                                mNavMenu.expandGroup(groupPosition);
                            break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        mNavMenu.expandGroup(6);    //默认将Lists这个组打开

        //------------------- Child Click监听 ---------------------------
        mNavMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                NavBean currentGroup = navBeanList.get(groupPosition);
                if(currentGroup.getmNavName().equals("Lists")){
                    if (mNavAdapter.isLastChild(childPosition)) {   //新建清单
                        //------ 这里显示对话框只是为测试功能用 -----
                        final EditText editText = new EditText(MainActivity.this);
                        editText.setHint(R.string.inputListName);
                        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.addList).setView(editText)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //检查清单是否存在
                                        String listName = editText.getText().toString();
                                        try {
                                            ListDao listDao = new ListDao(dataBaseHelper.getListDao());
                                            if(listDao.queryByUserIdAndListname(currentUser.getId(), listName) == null){
                                                //清单不存在，添加到数据库
                                                ListBean listBean = new ListBean(listName);
                                                listBean.setUser(currentUser);
                                                listDao.create(listBean);
                                                mNavAdapter.initData();
                                                mNavAdapter.notifyDataSetChanged();
                                            } else {    //清单已存在
                                                Toast.makeText(MainActivity.this, R.string.listExisted, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        //关闭对话框
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {    //查看清单内容
                        BaseEventListFragment fragment = (BaseEventListFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                        if (fragment instanceof EventListWithCollapseToolBarFragment)
                        {
                            fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
                            ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));

                        }
                        else {
                            try {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                                fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            }
                        }
                        drawer.closeDrawer(GravityCompat.START);    //关闭菜单
                    }
                } else if (currentGroup.getmNavName().equals("Closed Lists")) {
                    BaseEventListFragment fragment = (BaseEventListFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                    if (fragment instanceof EventListWithCollapseToolBarFragment)
                    {
                        fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
                        ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                    }
                    else {
                        try {
                            fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                            fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
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

        //初始加载fragment
        mNavMenu.setSelectedGroup(0);
        String[] arg = DateUtil.getQueryBetweenDay();
        try {
            EventListWithCollapseToolBarFragment fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), arg[0], arg[1]);
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment, "contentList").commit();
            fragment.setToolbarTitle("Today");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
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
