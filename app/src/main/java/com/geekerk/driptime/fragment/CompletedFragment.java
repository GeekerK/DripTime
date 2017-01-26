package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.CompletedEventExpandableListAdapter;
import com.geekerk.driptime.adapter.DataChangeListener;

/**
 * Created by s21v on 2016/6/14.
 */
public class CompletedFragment extends BaseEventListFragment implements DataChangeListener {
    private ExpandableListView mList;
    private CompletedEventExpandableListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        mList = (ExpandableListView) view.findViewById(android.R.id.list);
        emptyView = (TextView) view.findViewById(R.id.empty);
        initToolBar((Toolbar) view.findViewById(R.id.toolbar));
        mAdapter = new CompletedEventExpandableListAdapter(getContext(), queryLocalDatabase(), this);
        mList.setAdapter(mAdapter);
        //首次加载，展开全部组
        for (int i = 0; i < mAdapter.getGroupCount(); i++)
            mList.expandGroup(i);
        return view;
    }

    @Override
    public void emptyData() {
        mList.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void haveData() {
        emptyView.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
    }

    @Override
    public void resetData() {
        mAdapter.setData(queryLocalDatabase());
    }
}
