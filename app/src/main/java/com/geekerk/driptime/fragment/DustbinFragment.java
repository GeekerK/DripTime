package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DustbinEventListViewAdapter;

/**
 * Created by Administrator on 2016/6/9.
 */
public class DustbinFragment extends BaseEventListFragment {
    private static final String TAG = "DustbinFragment";
    private DustbinEventListViewAdapter mAdapter;

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

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new DustbinEventListViewAdapter(getContext(), queryLocalDatabase());
        listView.setAdapter(mAdapter);
        TextView emptyView = (TextView) view.findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dustbin_list, menu);
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
