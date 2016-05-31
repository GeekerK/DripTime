package com.geekerk.driptime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.EventBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter implements LinearLayoutWithAction.EventDealInterface{
    private static final String TAG = "RecyclerViewAdapter";
    private static final int CHANNEL_VIEW_TYPE = 1;
    private static final int EVENT_VIEW_TYPE = 2;
    private static final int EVENT_VIEW_TYPE_HAVE_DEADLINE = 3;

    private ArrayList<EventBean> databaseResult;
    private LinkedHashMap<String, ArrayList<EventBean>> data;   //栏目名称和对应的数据列表
    public SparseArray<String> channelData;    //存放栏目的位置和文本
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private int itemCount;

    public EventRecyclerViewAdapter(Context c, ArrayList<EventBean> data) {
        context = c;
        simpleDateFormat = new SimpleDateFormat("k:mm");
        setData(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CHANNEL_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_channel, parent, false);
            return new ChannelViewHolder(view);
        } else {
            LinearLayoutWithAction view;
            if (viewType == EVENT_VIEW_TYPE) {
                view = new LinearLayoutWithAction(context, R.layout.layout_event, R.dimen.layout_event_height);
                view.setEventDealInterface(this);
                return new EventViewHolder(view);
            } else {
                view = new LinearLayoutWithAction(context, R.layout.layout_event_with_deadline, R.dimen.layout_event_with_deadline_height);
                view.setEventDealInterface(this);
                return new EventHaveDeadlineViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelViewHolder) {
            ((ChannelViewHolder) holder).setTitle(channelData.get(position));
        } else {
            EventBean eventBean = getEventAtPosition(position);
            if (eventBean.isFinished()) {
                Log.i(TAG, "onBindViewHolder:"+eventBean.toString());
                ((EventViewHolder)holder).setFinish();
            }
            if (holder instanceof EventHaveDeadlineViewHolder)
                ((EventHaveDeadlineViewHolder) holder).setDeadlineTitle(simpleDateFormat.format(eventBean.getDeadline()));
            ((EventViewHolder) holder).setEventTitle(eventBean.getTitle());
            ((EventViewHolder) holder).setEventPriority(eventBean.getProrityColorRes());
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (channelData.indexOfKey(position) >= 0)
            return CHANNEL_VIEW_TYPE;
        else {
            if (getEventAtPosition(position).getDeadline() == null) {
                return EVENT_VIEW_TYPE;
            } else {
                return EVENT_VIEW_TYPE_HAVE_DEADLINE;
            }
        }
    }

    private EventBean getEventAtPosition(int position) {
        int currentIndex = -1;
        for (int i = 0; i < channelData.size(); i++) {
            if (position < channelData.keyAt(i)) {
                break;
            } else
                currentIndex++;
        }
        String currentChannel = channelData.valueAt(currentIndex); //所在的栏目
        ArrayList<EventBean> list = data.get(currentChannel);

        int offset = 1;
        for (int i = 0; i < currentIndex; i++) {
            offset += (data.get(channelData.valueAt(i)).size() + 1);  //设置偏移量
        }
        return list.get(position - offset);
    }

    //设置数据源
    public void setData(ArrayList<EventBean> databaseResult) {
        this.databaseResult = databaseResult;
        parseData();
        notifyDataSetChanged();
    }

    private void parseData() {
        data = new LinkedHashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 E");
        ArrayList<EventBean> dummyData = new ArrayList<>();
        ArrayList<EventBean> completeDate = new ArrayList<>();
        String lastTime = "";
        String time = "";
        for (EventBean event : databaseResult) {
            if (event.isFinished())
                completeDate.add(event);
            else {
                time = simpleDateFormat.format(event.getReleaseTime());
                if (event.getReleaseTime().getDate() == new Date().getDate())
                    time = "今天 " + time;
                if (lastTime.equals(""))
                    lastTime = time;
                if (!time.equals(lastTime)) {  //时间不同说明是新的时间事件序列开始了
                    data.put(lastTime, dummyData);  //保存之前的数据
                    dummyData = new ArrayList<>();
                    lastTime = time;
                }
                dummyData.add(event);
            }
        }
        if(!TextUtils.isEmpty(lastTime))
            data.put(lastTime, dummyData);
        data.put("已完成", completeDate);

        channelData = new SparseArray<>();
        itemCount = 0;
        for (String barTitleString : data.keySet()) {
            channelData.put(itemCount, barTitleString);
            itemCount += data.get(barTitleString).size() + 1;
        }

//        Log.i(TAG,data.toString());
    }

    //处理点击完成事件
    public void checkFinish(int position) {
        DataBaseHelper helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        EventBean eventBean = getEventAtPosition(position);
        eventBean.setFinished(!eventBean.isFinished());
        try {
            helper.getEventDao().update(eventBean);
            parseData();
            notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OpenHelperManager.releaseHelper();
            helper = null;
        }

        Log.i(TAG, "databaseResult:"+databaseResult.toString());
    }

    //移动
    public void moveEventAtPosition(int position) {

    }

    //删除
    public void deleteEventAtPosition(int position) {

    }

    //修改
    public void modifyEventAtPosition(int position) {

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
        View view;
        TextView eventTitle;
        View eventPriority;
        CheckBox eventFinish;

        public EventViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            eventTitle = (TextView) itemView.findViewById(R.id.event_title_tv);
            eventPriority = itemView.findViewById(R.id.event_priority);
            eventFinish = (CheckBox) itemView.findViewById(R.id.isDone_checkbox);
        }

        public void setEventTitle(String title) {
            eventTitle.setText(title);
        }

        public void setEventPriority(int colorRes) {
            eventPriority.setBackgroundResource(colorRes);
        }

        public void setFinish() {
            eventFinish.setChecked(true);
            eventFinish.setEnabled(false);
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
