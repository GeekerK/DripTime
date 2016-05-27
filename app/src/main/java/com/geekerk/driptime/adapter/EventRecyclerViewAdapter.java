package com.geekerk.driptime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.EventBean;

import java.util.ArrayList;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerViewAdapter";
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
                view = new LinearLayoutWithAction(context, R.layout.layout_event, R.dimen.layout_event_height);
                return new EventViewHolder(view);
            } else {
                view = new LinearLayoutWithAction(context, R.layout.layout_event_with_deadline, R.dimen.layout_event_with_deadline_height);
                return new EventHaveDeadlineViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelViewHolder) {
            if (position == 0)
                ((ChannelViewHolder)holder).setTitle("今天, 5月4日 星期三");
            else
                ((ChannelViewHolder)holder).setTitle("已完成");
        } else {
            Log.i(TAG, "position:"+position);
            EventBean eventBean;
            if (position-1 < data.size())
                eventBean = data.get(position-1);
            else
                eventBean = completeData.get(position-data.size()-2);
            if(holder instanceof  EventHaveDeadlineViewHolder)
                ((EventHaveDeadlineViewHolder)holder).setDeadlineTitle(eventBean.getDeadline());
            ((EventViewHolder)holder).setEventTitle(eventBean.getTitle());
            ((EventViewHolder)holder).setEventPriority(eventBean.getProrityColor());
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+completeData.size()+2;
    }

    private static final int CHANNEL_VIEW_TYPE = 1;
    private static final int EVENT_VIEW_TYPE = 2;
    private static final int EVENT_VIEW_TYPE_HAVE_DEADLINE = 3;

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
        TextView eventTitle;
        View eventPriority;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title_tv);
            eventPriority = itemView.findViewById(R.id.event_priority);
        }

        public void setEventTitle(String title) {
            eventTitle.setText(title);
        }

        public void setEventPriority(int colorRes)  {
            eventPriority.setBackgroundResource(colorRes);
        }
    }

    class EventHaveDeadlineViewHolder extends EventViewHolder {
        private TextView eventDeadline;

        public EventHaveDeadlineViewHolder(View itemView) {
            super(itemView);
            eventDeadline = (TextView) itemView.findViewById(R.id.event_deadline_tv);
        }

        public void setDeadlineTitle(String deadline) {
            eventDeadline.setText(deadline);
        }
    }
}
