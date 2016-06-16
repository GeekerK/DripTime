package com.geekerk.driptime.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DustbinEventListViewAdapter;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.vo.EventBean;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/9.
 */
public class DustbinFragment extends BaseEventListFragment {
    private static final String TAG = "DustbinFragment";
    private DustbinEventListViewAdapter mAdapter;
    private ArrayList<EventBean> data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dustbin, container, false);

        //设置toolbar
        initToolBar((Toolbar) view.findViewById(R.id.toolbar));

        data = queryLocalDatabase();
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new DustbinEventListViewAdapter(getContext(), data);
        listView.setAdapter(mAdapter);
        emptyView = (TextView) view.findViewById(R.id.empty);
        listView.setEmptyView(emptyView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)  //3.0一下注册上下文菜单
            registerForContextMenu(listView);
        else {  //3.0以后使用actionMode
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    mode.setTitle(getResources().getString(R.string.selected)+": "+listView.getCheckedItemCount());
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_dustbin_list,menu);
                    mAdapter.setIsLongPress(true);
                    mode.setTitle(getResources().getString(R.string.selected)+": "+listView.getCheckedItemCount());
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    Log.i(TAG, listView.getCheckedItemPositions().toString());
                    switch (item.getItemId()) {
                        case R.id.reply :
                            //恢复，删除清单为空
                            for (int i=mAdapter.getCount()-1; i>=0; i--) {  //从下向上操作原因是 mAdapter.removeItem(i) 会影响到 mAdapter.getCount() 的取值
                                if (listView.isItemChecked(i)) {
                                    EventBean selected = (EventBean) mAdapter.getItem(i);
                                    selected.setList(null);
                                    try {
                                        EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao()); //数据库操作
                                        eventDao.update(selected);
                                        mAdapter.removeItem(i); //更新缓存
                                        listView.setItemChecked(i, false);  //重置 listview 的 check 状态
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            return true;
                        case R.id.delete :
                            //从数据库中删除记录
                            for (int i=mAdapter.getCount()-1; i>=0; i--) {
                                if (listView.isItemChecked(i)) {
                                    EventBean selected = (EventBean) mAdapter.getItem(i);
                                    try {
                                        EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
                                        eventDao.delete(selected);
                                        mAdapter.removeItem(i);
                                        listView.setItemChecked(i, false);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mAdapter.setIsLongPress(false);
                }
            });
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_dustbin_list, menu);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dustbin_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        EventBean selected = (EventBean) mAdapter.getItem(position);
        // TODO: 2016/6/10 根据position去操作数据库
        switch (item.getItemId()) {
            case R.id.reply :
                //恢复，删除清单为空
                selected.setList(null);
                try {
                    EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao()); //数据库操作
                    eventDao.update(selected);
                    mAdapter.removeItem(position);
                    mAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.delete :
                //从数据库中删除记录
                try {
                    EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
                    eventDao.delete(selected);
                    mAdapter.removeItem(position);
                    mAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_empty) {
            try {
                EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
                eventDao.deleteCollection(mAdapter.getData());
                mAdapter.getData().clear();
                mAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mAdapter.getCount() == 0)
            menu.setGroupVisible(R.id.group, false);
        else
            menu.setGroupVisible(R.id.group, true);
    }

    @Override
    public void resetData() {

    }
}
