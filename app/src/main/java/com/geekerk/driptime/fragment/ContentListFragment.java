package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.view.FixedSpaceLinearLayout;

/**
 * Created by s21v on 2016/5/24.
 */
public class ContentListFragment extends Fragment {
    private static final String TAG = "ContentListFragment";
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contentlist, container, false);
        FixedSpaceLinearLayout fixedSpaceLinearLayout = (FixedSpaceLinearLayout) view.findViewById(R.id.contentList);
        listView = fixedSpaceLinearLayout.getContentList();
        listView.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, getDummyData()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "firstVisibleItem:"+listView.getFirstVisiblePosition());
            }
        });
        return view;
    }

    private String[] getDummyData() {
        String[] dummyData = new String[20];
        for (int i=1; i<=20; i++)
            dummyData[i-1] = "Data "+i;
        return dummyData;
    }
}
