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
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DustbinEventListViewAdapter;
import com.geekerk.driptime.vo.EventBean;

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

        //------ 设置toolbar ------
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        if (!TextUtils.isEmpty(mToolbarTitle))
            mToolbar.setTitle(mToolbarTitle);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        //------ end --------

        data = queryLocalDatabase();
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new DustbinEventListViewAdapter(getContext(), data);
        listView.setAdapter(mAdapter);
        TextView emptyView = (TextView) view.findViewById(R.id.empty);
        listView.setEmptyView(emptyView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)  //3.0一下注册上下文菜单
            registerForContextMenu(listView);
        else {
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

                        Log.i(TAG, "CheckedItemIds:"+listView.getCheckedItemPositions());
                    // TODO: 2016/6/10 根据position去操作数据库
                    switch (item.getItemId()) {
                        case R.id.reply :
                            //恢复，删除清单为空
                            return true;
                        case R.id.delete :
                            //从数据库中删除记录
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
        // TODO: 2016/6/10 根据position去操作数据库
        switch (item.getItemId()) {
            case R.id.reply :
                //恢复，删除清单为空
                return true;
            case R.id.delete :
                //从数据库中删除记录
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
