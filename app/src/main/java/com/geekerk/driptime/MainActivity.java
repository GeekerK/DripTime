package com.geekerk.driptime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.fragment.BaseEventListFragment;
import com.geekerk.driptime.fragment.ClosedListFragment;
import com.geekerk.driptime.fragment.CompletedFragment;
import com.geekerk.driptime.fragment.DustbinFragment;
import com.geekerk.driptime.fragment.EventListWithCollapseToolBarFragment;
import com.geekerk.driptime.fragment.ListFragment;
import com.geekerk.driptime.nav.NavAdapter;
import com.geekerk.driptime.utils.DateUtil;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.NavBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.onListChangeListener {
    private static final String TAG = "MainActivity";
    //查询指定用户在一段时间内的所有未放到垃圾箱的事件，按ID降序
    private static final String BASE_QUERY =
            "select * from table_event where userId = ? and (listId <> ? or listId is null) and release_time between datetime(?) and datetime(?) order by id DESC";
    //查询指定用户的所有未放到垃圾箱的事件，按ID降序
    private static final String QUERY_ALL = "select * from table_event where userId = ? and (listId <> ? or listId is null) order by id DESC";
    //查询指定用户未放到垃圾箱的所有已完成的事件
    private static final String QUERY_COMPLETED = "select * from table_event where userId = ? and isFinished = 1 and (listId <> ? or listId is null) order by id DESC";
    //查询指定用户的指定清单中的事件, 按ID降序
    private static final String QUERY_BY_LIST = "select * from table_event where userId = ? and listId = ? order by id DESC";

    //当前用户Id，当前用户垃圾箱，收件箱ID
    private int userId, dustinListId, collectBoxListId;
    private ExpandableListView mNavMenu;
    private DataBaseHelper dataBaseHelper;
    private UserBean currentUser;
    private DrawerLayout drawer;
    private NavAdapter mNavAdapter;
    //今天，7天，所有 未完成的事件数目
    private int mMsgNumToday, mMsgNumWeek, mMsgNumAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);

        if (savedInstanceState != null)
            currentUser = (UserBean) savedInstanceState.getSerializable("currentUser");
        else
            currentUser = (UserBean) getIntent().getSerializableExtra("currentUser");
        //设置用户名
        TextView userName = (TextView) findViewById(R.id.nav_username);
        //若不是用默认的账号登录，则显示用户名， 默认的账号是在用户跳过登录时的应用根据Build.DEVICE分配的。
        if (!(currentUser.getName().equals(Build.DEVICE) && currentUser.getEmail().equals(Build.DEVICE)))
            userName.setText(currentUser.getName());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavMenu = (ExpandableListView) drawer.findViewById(R.id.nav_menu);

        //初始化数据
        dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        //当前用户ID
        userId = currentUser.getId();
        //当前用户垃圾箱和收集箱的ListID
        dustinListId = 0;
        collectBoxListId = 0;
        try {
            ListDao listDao = new ListDao(dataBaseHelper.getListDao());
            dustinListId = listDao.queryByUserIdAndListname(userId, "垃圾桶").getId();
            collectBoxListId = listDao.queryByUserIdAndListname(userId, "收集箱").getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //获得未完成的事件数
        mMsgNumToday = getTodayMsgNum();
        mMsgNumWeek = getWeekMsgNum();
        mMsgNumAll = getAllMsgNum();

        //初始化菜单数据
        final List<NavBean> navBeanList = new ArrayList<>();
        NavBean navBeanToday = new NavBean(R.mipmap.nav_today, R.string.today, mMsgNumToday);
        final NavBean navBeanAll = new NavBean(R.mipmap.nav_all, R.string.all, mMsgNumAll);
        NavBean navBeanNearlySevenDays = new NavBean(R.mipmap.nav_nearlysevendays, R.string.nearly_seven_days, mMsgNumWeek);
        NavBean navBeanCollectionBox = new NavBean(R.mipmap.nav_collectionbox, R.string.collection_box, 0);
        NavBean navBeanCompleted = new NavBean(R.mipmap.nav_completed, R.string.completed, 0);
        NavBean navBeanDustbin = new NavBean(R.mipmap.nav_dustbin, R.string.dustbin, 0);
        NavBean navBeanLists = new NavBean(R.mipmap.nav_additem, R.string.lists, 0);
        NavBean navBeanClosedLists = new NavBean(R.mipmap.nav_delete, R.string.closed_lists, 0);
        NavBean navBeanSettings = new NavBean(R.mipmap.nav_settings, R.string.settings, 0);
        navBeanList.add(navBeanToday);
        navBeanList.add(navBeanAll);
        navBeanList.add(navBeanNearlySevenDays);
        navBeanList.add(navBeanCollectionBox);
        navBeanList.add(navBeanCompleted);
        navBeanList.add(navBeanDustbin);
        navBeanList.add(navBeanLists);
        navBeanList.add(navBeanClosedLists);
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
                            if (fragment instanceof EventListWithCollapseToolBarFragment) {
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
                            } else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 1: //All
                            if (fragment instanceof EventListWithCollapseToolBarFragment) {
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId));
                            } else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_ALL, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 2: //week
                            String[] args1 = DateUtil.getQueryBetweenWeek();
                            if (fragment instanceof EventListWithCollapseToolBarFragment) {
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]);
                            } else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args1[0], args1[1]);
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 3: //Collection Box
                            if (fragment instanceof EventListWithCollapseToolBarFragment) {
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                ((EventListWithCollapseToolBarFragment) fragment).changeData(QUERY_BY_LIST, String.valueOf(userId), String.valueOf(collectBoxListId));
                            } else {
                                fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(collectBoxListId));
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 4: //Completed
                            if (!(fragment instanceof CompletedFragment)) {
                                fragment = BaseEventListFragment.getInstance(CompletedFragment.class, drawer, QUERY_COMPLETED, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 5: //Dustbin
                            if (!(fragment instanceof DustbinFragment)) {
                                fragment = BaseEventListFragment.getInstance(DustbinFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(dustinListId));
                                fragment.setToolbarTitle(getResources().getString(navBeanList.get(groupPosition).getNavNameResource()));
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
                            }
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        case 6: //Lists
                        case 7: //Closed Lists
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

        mNavMenu.expandGroup(6);    //默认将Lists（清单）这个组打开

        //------------------- Child Click监听 ---------------------------
        mNavMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                NavBean currentGroup = navBeanList.get(groupPosition);
                if (currentGroup.getNavNameResource() == R.string.lists) {
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
                                            if (listDao.queryByUserIdAndListname(currentUser.getId(), listName) == null) {
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
                        if (fragment.getClass() == ListFragment.class) {
                            Log.i(TAG, "查看清单内容 1 , groupPosition:" + groupPosition + ", childPosition:" + childPosition + " , mNavAdapter.getListId(groupPosition, childPosition):" + mNavAdapter.getListId(groupPosition, childPosition));
                            fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
//                            ((ListFragment) fragment).setListChangeListener(MainActivity.this);
                            ((ListFragment) fragment).changeData(QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                        } else {
                            try {
                                Log.i(TAG, "查看清单内容 2 , groupPosition:" + groupPosition + ", childPosition:" + childPosition + " , mNavAdapter.getListId(groupPosition, childPosition):" + mNavAdapter.getListId(groupPosition, childPosition));
                                fragment = BaseEventListFragment.getInstance(ListFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                                ((ListFragment) fragment).setListChangeListener(MainActivity.this);
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
                } else if (currentGroup.getNavNameResource() == R.string.closed_lists) {
                    BaseEventListFragment fragment = (BaseEventListFragment) getSupportFragmentManager().findFragmentByTag("contentList");
                    if (fragment instanceof ClosedListFragment) {
                        fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
//                        ((ClosedListFragment) fragment).setListChangeListener(MainActivity.this);
                        ((ClosedListFragment) fragment).changeData(QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                    } else {
                        try {
                            fragment = BaseEventListFragment.getInstance(ClosedListFragment.class, drawer, QUERY_BY_LIST, String.valueOf(userId), String.valueOf(mNavAdapter.getListId(groupPosition, childPosition)));
                            fragment.setToolbarTitle(mNavAdapter.getListName(groupPosition, childPosition));
                            ((ClosedListFragment) fragment).setListChangeListener(MainActivity.this);
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

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {   //检查更新时间数目
                mMsgNumToday = getTodayMsgNum();
                mMsgNumWeek = getWeekMsgNum();
                mMsgNumAll = getAllMsgNum();
                if (navBeanList.get(0).getmMsgNum() != mMsgNumToday)
                    navBeanList.get(0).setmMsgNum(mMsgNumToday);
                if (navBeanList.get(1).getmMsgNum() != mMsgNumAll)
                    navBeanList.get(1).setmMsgNum(mMsgNumAll);
                if (navBeanList.get(2).getmMsgNum() != mMsgNumWeek)
                    navBeanList.get(2).setmMsgNum(mMsgNumWeek);
                mNavAdapter.setmGroups(navBeanList);
                mNavAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

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

    private int getAllMsgNum() {
        try {
            return (int) dataBaseHelper.getEventDao().queryRawValue(
                    "select count(*) from table_event where userId = ? and (listId <> ? or listId is null) and isFinished = 0",
                    String.valueOf(currentUser.getId()), String.valueOf(dustinListId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getWeekMsgNum() {
        try {
            String[] timeArgs = DateUtil.getQueryBetweenWeek();
            return (int) dataBaseHelper.getEventDao().queryRawValue(
                    "select count(*) from table_event where userId = ? and (listId <> ? or listId is null) and isFinished = 0 and (release_time between datetime(?) and datetime(?))",
                    String.valueOf(currentUser.getId()), String.valueOf(dustinListId), timeArgs[0], timeArgs[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //查询当前一天内未完成的消息数目
    private int getTodayMsgNum() {
        try {
            String[] timeArgs = DateUtil.getQueryBetweenDay();
            return (int) dataBaseHelper.getEventDao().queryRawValue(
                    "select count(*) from table_event where userId = ? and (listId <> ? or listId is null) and isFinished = 0 and release_time between datetime(?) and datetime(?)",
                    String.valueOf(userId), String.valueOf(dustinListId), timeArgs[0], timeArgs[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    @Override
    public void listUpdate() {
        mNavAdapter.initData();
        mNavAdapter.notifyDataSetChanged();
    }

    @Override
    public void listDelete() {
        listUpdate();
        //切换fragment
        String[] args = DateUtil.getQueryBetweenDay();
        EventListWithCollapseToolBarFragment fragment = null;
        try {
            fragment = BaseEventListFragment.getInstance(EventListWithCollapseToolBarFragment.class, drawer, BASE_QUERY, String.valueOf(userId), String.valueOf(dustinListId), args[0], args[1]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        fragment.setToolbarTitle(getResources().getString(R.string.today));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "contentList").commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            BaseEventListFragment fragment = (BaseEventListFragment) getSupportFragmentManager().findFragmentByTag("contentList");
            fragment.resetData();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
