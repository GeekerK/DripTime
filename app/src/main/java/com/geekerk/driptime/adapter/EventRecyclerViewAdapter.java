package com.geekerk.driptime.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.R;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by s21v on 2016/5/26.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter implements LinearLayoutWithAction.EventDealInterface {
    private static final String TAG = "RecyclerViewAdapter";
    private static final int CHANNEL_VIEW_TYPE = 1;
    private static final int EVENT_VIEW_TYPE = 2;
    private static final int EVENT_VIEW_TYPE_HAVE_DEADLINE = 3;
    public SparseArray<String> channelData;    //存放栏目的位置和文本
    private ArrayList<EventBean> dataFromDB;
    private LinkedHashMap<String, ArrayList<EventBean>> data;   //栏目名称和对应的数据列表
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private int itemCount;

    public EventRecyclerViewAdapter(Context c, ArrayList<EventBean> dataFromDB, DataChangeListener listener) {
        context = c;
        simpleDateFormat = new SimpleDateFormat("k:mm");
        mDataChangeListener = listener;
        setData(dataFromDB);
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
            //查询栏目下的数据，若为空则隐藏栏目名这一栏，主要是已完成有可能为空
            String channelName = channelData.get(position);
            if (channelName != null) {
                if(data.get(channelName).size() == 0)
                    ((ChannelViewHolder) holder).setVisible(View.GONE);
                else
                    ((ChannelViewHolder) holder).setVisible(View.VISIBLE);
            }
        } else {
            EventBean eventBean = getEventAtPosition(position);
            ((EventViewHolder) holder).initScrollX();
            ((EventViewHolder) holder).setEventTitle(eventBean.getTitle());
            ((EventViewHolder) holder).setEventPriority(eventBean.getProrityColorRes());
            if (eventBean.isFinished()) {
                ((EventViewHolder) holder).setEventFinish(true);
            } else {
                ((EventViewHolder) holder).setEventFinish(false);
                if (holder instanceof EventHaveDeadlineViewHolder)
                    ((EventHaveDeadlineViewHolder) holder).setDeadlineTitle(simpleDateFormat.format(eventBean.getDeadline()));
            }
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

    private int getPositionById(int id) {
        int position = 0;
        for (int i = 0; i < data.size(); i++) {
            ArrayList<EventBean> list = data.get(channelData.valueAt(i));
            for (int j = 0; j < list.size(); j++) {
                position++;
                EventBean eventBean = list.get(j);
                if (eventBean.getId() == id)
                    return position;
            }
            position++;
        }
        return 0;
    }

    //设置数据源
    public void setData(ArrayList<EventBean> dataFromDB) {
        this.dataFromDB = dataFromDB;
        parseData();
        notifyDataSetChanged();
    }

    //将数据按 时间 和 是否完成 分类
    private void parseData() {
        data = new LinkedHashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 E");
        ArrayList<EventBean> dummyData = new ArrayList<>();
        ArrayList<EventBean> completeDate = new ArrayList<>();
        String lastTime = "";
        String time;
        for (EventBean event : dataFromDB) {
            if (event.isFinished()) //已完成
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
        if (!TextUtils.isEmpty(lastTime))
            data.put(lastTime, dummyData);
        data.put("已完成", completeDate);
        channelData = new SparseArray<>();
        itemCount = 0;  //计算RecyclerView的ItemCount， 当ItemCount = 1认为是没有数据的（即只有已完成栏目那一栏）
        for (String barTitleString : data.keySet()) {
            channelData.put(itemCount, barTitleString);
            itemCount += data.get(barTitleString).size() + 1;
        }
        //数据为空，通知监听器
        if(itemCount <= 1)
            mDataChangeListener.emptyData();
        else
            mDataChangeListener.haveData();
    }

    //处理点击完成事件
    public void checkFinish(int position) {
        EventBean eventBean = getEventAtPosition(position);
        eventBean.setFinished(!eventBean.isFinished());
        DataBaseHelper helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        try {
            helper.getEventDao().update(eventBean);
            parseData();
            notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
            eventBean.setFinished(!eventBean.isFinished()); //出错还原操作
        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    //移动
    public void moveEventAtPosition(int position) {
        //获得用户的清单列表
        int userId = context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("currentUserID", -1);
        DataBaseHelper helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        List<ListBean> userList = null;
        try {
            ListDao listDao = new ListDao(helper.getListDao());
            userList = listDao.queryByUserId(userId, false);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OpenHelperManager.releaseHelper();
            helper = null;
        }

        final EventBean eventBean = getEventAtPosition(position);
        int checkItem = userList.indexOf(eventBean.getList());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final List<ListBean> finalUserList = userList;
        builder.setTitle(R.string.move2list).setSingleChoiceItems(new ListBeanAdapter(userList, context), checkItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataBaseHelper helper1 = OpenHelperManager.getHelper(context, DataBaseHelper.class);
                        ListBean selectedList = finalUserList.get(which);
                        try {
                            EventDao eventDao = new EventDao(helper1.getEventDao());
                            eventBean.setList(selectedList);
                            eventDao.update(eventBean);
                            dialog.dismiss();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            OpenHelperManager.releaseHelper();
                            helper1 = null;
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    //删除
    public void deleteEventAtPosition(int position) {
        final EventBean eventBean = getEventAtPosition(position);
        final DataBaseHelper helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        final int userId = sharedPreferences.getInt("currentUserID", -1);
        try {
            //事务处理：将事件放入垃圾箱
            int result = TransactionManager.callInTransaction(helper.getConnectionSource(), new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    ListDao listDao = new ListDao(helper.getListDao());
                    ListBean dustbinList = listDao.queryByUserIdAndListname(userId, "垃圾桶");
                    eventBean.setList(dustbinList);
                    EventDao eventDao = new EventDao(helper.getEventDao());
                    eventDao.update(eventBean);
                    return 1;
                }
            });
            if (result == 1) {
                //在删除缓存数据
                dataFromDB.remove(eventBean);
                //更新数据源
                parseData();
                notifyDataSetChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(context, "Oops! This is Error!", Toast.LENGTH_SHORT).show();
        }
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

        public void setVisible(int visible) {
            ((View)textview.getParent()).setVisibility(visible);
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        View eventPriority;
        CheckBox eventFinish;
        View linearWithAction;

        public EventViewHolder(View itemView) {
            super(itemView);
            linearWithAction = itemView;
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

        public void setEventFinish(boolean isFinish) {
            eventFinish.setChecked(isFinish);
            if (isFinish) {
                eventTitle.setTextColor(Color.argb(89, 0, 0, 0));
            } else {
                eventTitle.setTextColor(Color.BLACK);
            }
        }

        public void initScrollX() {
            linearWithAction.setScrollX(0);
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

    private DataChangeListener mDataChangeListener;

    public interface DataChangeListener {
        void emptyData();
        void haveData();
    }
}
