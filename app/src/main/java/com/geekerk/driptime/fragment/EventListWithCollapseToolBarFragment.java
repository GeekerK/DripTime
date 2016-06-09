package com.geekerk.driptime.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.EventRecyclerViewAdapter;
import com.geekerk.driptime.view.LinearLayoutWithAction;

/**
 * Created by s21v on 2016/5/24.
 */
public class EventListWithCollapseToolBarFragment extends BaseEventListFragment {
    private static final String TAG = "CollapseToolBarFragment";
    private RecyclerView recyclerView;
    private EventRecyclerViewAdapter mAdapter;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        if (!TextUtils.isEmpty(mToolbarTitle))
            mCollapsingToolbarLayout.setTitle(mToolbarTitle);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new EventRecyclerViewAdapter(getActivity(), queryLocalDatabase());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {  //添加分割线
            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setARGB(89, 0, 0, 0);
                paint.setStrokeWidth(1);
                for (int i = 0; i < parent.getChildCount() - 1; i++) {
                    View view = parent.getChildAt(i);
                    if (view instanceof LinearLayoutWithAction && mAdapter.channelData.indexOfKey(i + 1) < 0)
                        c.drawLine(0, view.getBottom(), view.getWidth(), view.getBottom(), paint);
                }
            }
        });

        //------ 设置toolbar ------
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        //------ end --------

        //快速新建按钮
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_common_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_edit) {

        }
        return super.onOptionsItemSelected(item);
    }

    //改变查询条件，重新读取加载数据
    public void changeData(String baseQuery, String... queryArgs) {
        mCollapsingToolbarLayout.setTitle(mToolbarTitle);
        mQuery = baseQuery;
        this.mQueryArgs = queryArgs;
        mAdapter.setData(queryLocalDatabase());
    }
}
