package com.geekerk.driptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.vo.ListBean;
import java.util.List;

/**
 * 用户的订单适配器，用在对话框
 * Created by s21v on 2016/6/7.
 */
public class ListBeanAdapter extends BaseAdapter {
    List<ListBean> data;
    Context context;
    int currentCheckItem;

    public ListBeanAdapter(List<ListBean> data, Context context, int currentCheckItem) {
        this.data = data;
        this.context = context;
        this.currentCheckItem = currentCheckItem;
    }

    @Override
    public int getCount() {
        return data.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_single_choice, parent, false);
            holder = new ViewHolder();
            holder.textView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0)
            holder.textView.setText(R.string.noList);
        else
            holder.textView.setText(data.get(position-1).getName());
        holder.textView.setChecked(currentCheckItem == position);
        return convertView;
    }

    class ViewHolder {
        CheckedTextView textView;
    }
}
