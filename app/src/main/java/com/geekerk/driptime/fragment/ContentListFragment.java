package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.EventRecyclerViewAdapter;
import com.geekerk.driptime.view.FixedSpaceLinearLayout;
import com.geekerk.driptime.vo.EventBean;

import java.util.ArrayList;

/**
 * Created by s21v on 2016/5/24.
 */
public class ContentListFragment extends Fragment {
    private static final String TAG = "ContentListFragment";
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_contentlist, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new EventRecyclerViewAdapter(getActivity(), getDummyData()));
        return recyclerView;
    }

    private ArrayList<EventBean> getDummyData() {
        ArrayList<EventBean> dummyData = new ArrayList<>();
        for (int i=1; i<=10; i++) {
            dummyData.add(new EventBean("待办事项 "+i, i%3==0 ? i+":0"+i:null));
        }
        return dummyData;
    }
}
