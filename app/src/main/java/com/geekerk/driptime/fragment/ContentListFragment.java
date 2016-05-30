package com.geekerk.driptime.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    //数据，第一个泛型是一天为单位的时间，第二个泛型是当天的事件
    private LinkedHashMap<String, ArrayList<EventBean>> data;

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
        data = new LinkedHashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_contentlist, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new EventRecyclerViewAdapter(getActivity(), getDummyData());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int position = parent.getChildAdapterPosition(view);
//                if (mAdapter.channelData.indexOfKey(position)<0) {
//                    if (mAdapter.channelData.indexOfKey(position+1)<0 && position!=parent.getChildCount()-1)
//                        outRect.set(0, 0, 0, 2);
//                }
//            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setARGB(89, 100, 255, 255);
                for (int i=0; i<parent.getChildCount()-1; i++) {
                    View view = parent.getChildAt(i);
                    if (view instanceof LinearLayoutWithAction && mAdapter.channelData.indexOfKey(i+1)<0)
                        c.drawLine(0, view.getBottom(), view.getWidth(), view.getBottom()+20, paint);
                }
            }
        });
        return recyclerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query",query);
    }

    //从本地数据库获得数据
    private LinkedHashMap<String, ArrayList<EventBean>> getDummyData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 E");
        ArrayList<EventBean> dummyData = new ArrayList<>();
        ArrayList<EventBean> completeDate = new ArrayList<>();
        String lastTime = "";
        String time = "";
        try {
            GenericRawResults<EventBean> results = dataBaseHelper.getEventDao().queryRaw(query, new EventRawRowMapper() ,queryArgs);
            for(EventBean event : results) {
                if (event.isFinished())
                    completeDate.add(event);
                else {
                    time = simpleDateFormat.format(event.getReleaseTime());
                    if (lastTime.equals(""))
                        lastTime = time;
                    if (!time.equals(lastTime)) {  //时间不同说明是新的时间事件序列开始了
                        if (event.getReleaseTime().getDate() == new Date().getDate())
                            time = "今天 "+time;
                        data.put(time, dummyData);  //保存之前的数据
                        dummyData.clear();  //清理
                        lastTime = time;
                    }
                    dummyData.add(event);
                }
            }
            data.put(time, dummyData);
            data.put("已完成", completeDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
