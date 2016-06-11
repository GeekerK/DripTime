package com.geekerk.driptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.vo.EventBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/9.
 */
public class DustbinEventListViewAdapter extends BaseAdapter {
    private ArrayList<EventBean> mData;
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat mSimpleDateFormat;
    private boolean mIsLongPress;

    public DustbinEventListViewAdapter(Context context, ArrayList<EventBean> mData) {
        mIsLongPress = false;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(context);
        mSimpleDateFormat = new SimpleDateFormat("MM-dd E");
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DustbinViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_dustbin_event, parent, false);
            viewHolder = new DustbinViewHolder();
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.event_title_tv);
            viewHolder.mPriority = convertView.findViewById(R.id.event_priority);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.time_tv);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.isDone_checkbox);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (DustbinViewHolder) convertView.getTag();
        EventBean eventBean = mData.get(position);
        viewHolder.mTitle.setText(eventBean.getTitle());
        viewHolder.mPriority.setBackgroundResource(eventBean.getProrityColorRes());
        viewHolder.mTime.setText(mSimpleDateFormat.format(eventBean.getReleaseTime()));
        if (mIsLongPress) {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mCheckBox.setChecked(((ListView)parent).isItemChecked(position));
        } else {
            viewHolder.mCheckBox.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void setIsLongPress(boolean mIsLongPress) {
        this.mIsLongPress = mIsLongPress;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
    }

    public ArrayList<EventBean> getData() {
        return mData;
    }

    class DustbinViewHolder {
        TextView mTitle;
        TextView mTime;
        View mPriority;
        CheckBox mCheckBox;
    }
}
