package com.geekerk.driptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.geekerk.driptime.vo.ListBean;
import java.util.List;

/**
 * 用户的订单适配器，用在对话框
 * Created by s21v on 2016/6/7.
 */
public class ListBeanAdapter extends BaseAdapter {
    List<ListBean> data;
    Context context;

    public ListBeanAdapter(List<ListBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position).getName());
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
