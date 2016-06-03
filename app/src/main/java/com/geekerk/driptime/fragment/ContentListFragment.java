package com.geekerk.driptime.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.geekerk.driptime.MainActivity;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.EventRecyclerViewAdapter;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventRawRowMapper;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.EventBean;
import com.j256.ormlite.dao.GenericRawResults;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by s21v on 2016/5/24.
 */
public class ContentListFragment extends Fragment {
    private static final String TAG = "ContentListFragment";
    private RecyclerView recyclerView;
    private DataBaseHelper dataBaseHelper;
    private String query;
    private String[] queryArgs;
    private EventRecyclerViewAdapter mAdapter;

    public static ContentListFragment getInstance(String query, String... query_Args) {
        ContentListFragment fragment = new ContentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        bundle.putStringArray("query_args", query_Args);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseHelper = ((MainActivity)getActivity()).getDataBaseHelper();
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
            queryArgs = savedInstanceState.getStringArray("query_args");
        }
        else {
            query = getArguments().getString("query");
            queryArgs = getArguments().getStringArray("query_args");
        }
        setRetainInstance(true);
        queryLocalDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_contentlist, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new EventRecyclerViewAdapter(getActivity(), queryLocalDatabase());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {  //添加分割线
            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setARGB(89, 0, 0, 0);
                paint.setStrokeWidth(1);
                for (int i=0; i<parent.getChildCount()-1; i++) {
                    View view = parent.getChildAt(i);
                    if (view instanceof LinearLayoutWithAction && mAdapter.channelData.indexOfKey(i+1)<0)
                        c.drawLine(0, view.getBottom(), view.getWidth(), view.getBottom(), paint);
                }
            }
        });
        return recyclerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
        outState.putStringArray("query_args", queryArgs);
    }

    //从本地数据库获得数据
    private ArrayList<EventBean> queryLocalDatabase() {
        ArrayList<EventBean> data = new ArrayList<>();
        try {
            GenericRawResults<EventBean> results = dataBaseHelper.getEventDao().queryRaw(query, new EventRawRowMapper() ,queryArgs);
            data.addAll(results.getResults());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    //改变查询条件，重新读取加载数据
    public void changeData(String baseQuery, String... queryArgs) {
        query = baseQuery;
        this.queryArgs = queryArgs;
        mAdapter.setData(queryLocalDatabase());
    }
}
