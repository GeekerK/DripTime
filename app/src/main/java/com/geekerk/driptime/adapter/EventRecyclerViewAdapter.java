package com.geekerk.driptime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.EventBean;

import java.util.ArrayList;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter {
    private ArrayList<EventBean> data;
    private ArrayList<EventBean> completeData;
    private Context context;

    public EventRecyclerViewAdapter(Context c, ArrayList<EventBean> data) {
        this.data = data;
        completeData = new ArrayList<>();
        context = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CHANNEL_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_channel, parent, false);
            return new ChannelViewHolder(view);
        } else {
            View view;
            if (viewType == EVENT_VIEW_TYPE) {
                view = new LinearLayoutWithAction(context, R.layout.layout_event);
            } else {
                view = new LinearLayoutWithAction(context, R.layout.layout_event_with_deadline);
            }
            return new EventViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelViewHolder) {
            if (position == 0)
                ((ChannelViewHolder)holder).setTitle("今天, 5月4日 星期三");
            else
                ((ChannelViewHolder)holder).setTitle("已完成");
        } else if (holder instanceof EventViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size()+completeData.size()+2;
    }

    private static final int CHANNEL_VIEW_TYPE = 1;
    private static final int EVENT_VIEW_TYPE = 2;
    private static final int EVENT_VIEW_TYPE_HAVE_DEADLINE = 2;

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1+data.size())
            return CHANNEL_VIEW_TYPE;
        else {
            if (data.get(position-1).getDeadline() == null) {
                return EVENT_VIEW_TYPE;
            } else {
                return EVENT_VIEW_TYPE_HAVE_DEADLINE;
            }
        }
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        TextView textview;
        public ChannelViewHolder(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.channel_title);
        }

        public void setTitle(String title) {
            textview.setText(title);
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutWithAction layoutView;

        public EventViewHolder(View itemView) {
            super(itemView);
            layoutView = (LinearLayoutWithAction) itemView;
        }
    }
}
