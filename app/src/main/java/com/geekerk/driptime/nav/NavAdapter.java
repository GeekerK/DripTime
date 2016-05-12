package com.geekerk.driptime.nav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.utils.LayoutUtil;

import java.util.List;

/**
 * Created with Android Studio IDEA
 * Author：LiYan
 * Date：2016/5/12
 * Time：14:52
 */
public final class NavAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mGroups;
    private List<List<String>> mItems;

    public NavAdapter(Context context, List<String> groups, List<List<String>> items) {
        mContext = context;
        mGroups = groups;
        mItems = items;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.nav_groups, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutUtil.getPixelByDIP(mContext, 60)));
        TextView textView = (TextView) view.findViewById(R.id.nav_groups_name);
        textView.setText(mGroups.get(groupPosition));
        return view;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItems.get(groupPosition).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mItems.get(groupPosition).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.nav_groups, null);
        TextView textView = (TextView) view.findViewById(R.id.nav_groups_name);
        textView.setText(mItems.get(groupPosition).get(childPosition));
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
