package com.geekerk.driptime.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DataChangeListener;
import com.geekerk.driptime.adapter.EventRecyclerViewAdapter;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.ListBean;

import java.sql.SQLException;

/**
 * Created by s21v on 2016/5/24.
 */
public class ListFragment extends BaseEventListFragment implements DataChangeListener {
    private static final String TAG = "CollapseToolBarFragment";
    protected RecyclerView recyclerView;
    protected EventRecyclerViewAdapter mAdapter;
    protected Toolbar mToolbar;
    protected ListBean mCurrentList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new EventRecyclerViewAdapter(getActivity(), queryLocalDatabase(), this, true);
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

        //设置toolbar
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolBar(mToolbar);

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
        inflater.inflate(R.menu.fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_list_edit) { //编辑清单
            if (mCurrentList != null) {
                // TODO: 2016/6/15 编辑清单
            }
        } else if (id == R.id.menu_list_close) { //关闭清单
            if (mCurrentList != null) {
                try {
                    ListDao listDao = new ListDao(mDatabaseHelper.getListDao());
                    mCurrentList.setClosed(true);
                    listDao.update(mCurrentList);
                    Toast.makeText(getContext(), String.format(getResources().getString(R.string.listclose_success), mToolbarTitle), Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //改变查询条件，重新读取加载数据
    public void changeData(String baseQuery, String... queryArgs) {
        mToolbar.setTitle(mToolbarTitle);
        mQuery = baseQuery;
        this.mQueryArgs = queryArgs;
        mAdapter.setData(queryLocalDatabase());
    }

    @Override
    public void emptyData() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(String.format(getResources().getString(R.string.listEmptyFormat), mToolbarTitle));
    }

    @Override
    public void haveData() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        //查询当前用户
        int userId = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE).getInt("currentUserID", -1);
        //根据清单名查询清单
        try {
            ListDao listDao = new ListDao(mDatabaseHelper.getListDao());
            mCurrentList = listDao.queryByUserIdAndListname(userId, mToolbarTitle);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
