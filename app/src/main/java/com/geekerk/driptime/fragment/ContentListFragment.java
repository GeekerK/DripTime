package com.geekerk.driptime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.geekerk.driptime.R;

/**
 * Created by s21v on 2016/5/24.
 */
public class ContentListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contentlist, container, false);
        ListView listView = (ListView) view.findViewById(R.id.contentList);
        listView.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, getDummyData()));
        return view;
    }

    private String[] getDummyData() {
        String[] dummyData = new String[20];
        for (int i=1; i<=20; i++)
            dummyData[i-1] = "Data "+i;
        return dummyData;
    }
}
