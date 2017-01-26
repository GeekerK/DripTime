package com.geekerk.driptime.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.AddItemActivity;
import com.geekerk.driptime.MainActivity;
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
 * 已完成Adapter
 * Created by s21v on 2016/6/14.
 */
public class CompletedEventExpandableListAdapter extends BaseExpandableListAdapter implements LinearLayoutWithAction.EventDealInterface {
    private static final String TAG = "CompletedAdapter";
    private ArrayList<EventBean> mDataFromDB;
    private LinkedHashMap<String, ArrayList<EventBean>> mData;   //栏目名称和对应的数据列表
    private ArrayList<String> mChannelData;    //存放栏目名称
    private Context mContext;
    private DataChangeListener mDataChangeListener;

    public CompletedEventExpandableListAdapter(Context context, ArrayList<EventBean> dataFromDB, DataChangeListener dataChangeListener) {
        mContext = context;
        mDataChangeListener = dataChangeListener;
        mDataFromDB = dataFromDB;
        parseData();
    }

    private void parseData() {
        mData = new LinkedHashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 E");
        ArrayList<EventBean> dummyData = new ArrayList<>();
        String lastTime = "";
        String time;
        for (EventBean event : mDataFromDB) {
            time = simpleDateFormat.format(event.getReleaseTime());
            if (event.getReleaseTime().getDate() == new Date().getDate())
                time = "今天 " + time;
            if (lastTime.equals(""))
                lastTime = time;
            if (!time.equals(lastTime)) {  //时间不同说明是新的时间事件序列开始了
                mData.put(lastTime, dummyData);  //保存之前的数据
                dummyData = new ArrayList<>();
                lastTime = time;
            }
            dummyData.add(event);
        }
        if (!TextUtils.isEmpty(lastTime))
            mData.put(lastTime, dummyData);
        mChannelData = new ArrayList<>(mData.keySet());

        //数据为空，通知监听器
        if (mData.size() == 0)
            mDataChangeListener.emptyData();
        else
            mDataChangeListener.haveData();
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(mChannelData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mChannelData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(mChannelData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_completed, parent, false);
            viewHolder = new GroupViewHolder();
            viewHolder.channelTitle = (TextView) convertView.findViewById(R.id.channel_title);
            viewHolder.groupExpand = (ImageView) convertView.findViewById(R.id.expand);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        viewHolder.channelTitle.setText(mChannelData.get(groupPosition));
        viewHolder.groupExpand.setSelected(isExpanded);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView == null) {
            convertView = new LinearLayoutWithAction(mContext, R.layout.layout_event, R.dimen.layout_event_height);
            ((LinearLayoutWithAction) convertView).setEventDealInterface(this);
            viewHolder = new ChildViewHolder();
            viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title_tv);
            viewHolder.eventFinish = (CheckBox) convertView.findViewById(R.id.isDone_checkbox);
            viewHolder.eventPriority = convertView.findViewById(R.id.event_priority);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
        convertView.setScrollX(0);
        EventBean eventBean = (EventBean) getChild(groupPosition, childPosition);
        viewHolder.eventTitle.setText(eventBean.getTitle());
        viewHolder.eventPriority.setBackgroundResource(eventBean.getProrityColorRes());
        viewHolder.eventFinish.setChecked(true);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private EventBean getEventAtPosition(int position) {
        int current = 1;
        for (int i = 0; i < mChannelData.size(); i++, current++) {
            ArrayList<EventBean> list = mData.get(mChannelData.get(i));
            for (int j = 0; j < list.size(); j++) {
                if (current == position) {
                    return list.get(j);
                } else
                    current++;
            }
        }
        return null;
    }

    @Override
    public void checkFinish(LinearLayoutWithAction view) {
        int position = ((ExpandableListView) view.getParent()).getPositionForView(view);
        EventBean eventBean = getEventAtPosition(position);
        eventBean.setFinished(!eventBean.isFinished());
        DataBaseHelper helper = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
        try {
            helper.getEventDao().update(eventBean);
            mDataFromDB.remove(eventBean);
            parseData();
            notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
            eventBean.setFinished(!eventBean.isFinished()); //出错还原操作
        } finally {
            OpenHelperManager.releaseHelper();
        }
    }

    @Override
    public void moveEventAtPosition(LinearLayoutWithAction view) {
        int position = ((ExpandableListView) view.getParent()).getPositionForView(view);
        //获得用户的清单列表
        int userId = mContext.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("currentUserID", -1);
        DataBaseHelper helper = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final List<ListBean> finalUserList = userList;
        builder.setTitle(R.string.move2list).setSingleChoiceItems(new ListBeanAdapter(userList, mContext, checkItem), checkItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataBaseHelper helper1 = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
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

    @Override
    public void deleteEventAtPosition(LinearLayoutWithAction view) {
        int position = ((ExpandableListView) view.getParent()).getPositionForView(view);
        final EventBean eventBean = getEventAtPosition(position);
        final DataBaseHelper helper = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        final int userId = sharedPreferences.getInt("currentUserID", -1);
        try {
            //事务处理：将事件放入垃圾箱
            int result = TransactionManager.callInTransaction(helper.getConnectionSource(), new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    //获得用户垃圾桶的清单
                    ListDao listDao = new ListDao(helper.getListDao());
                    ListBean dustbinList = listDao.queryByUserIdAndListname(userId, "垃圾桶");
                    //更新事件所在的清单
                    eventBean.setList(dustbinList);
                    EventDao eventDao = new EventDao(helper.getEventDao());
                    eventDao.update(eventBean);
                    return 1;
                }
            });
            if (result == 1) {
                //在删除缓存数据
                mDataFromDB.remove(eventBean);
                //更新数据源
                parseData();
                notifyDataSetChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Oops! This is Error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void modifyEventAtPosition(LinearLayoutWithAction view) {
        int position = ((ExpandableListView) view.getParent()).getPositionForView(view);
        EventBean eventBean = getEventAtPosition(position);
        Intent intent = new Intent(mContext, AddItemActivity.class);
        intent.putExtra("modifyEvent", eventBean);
        ((MainActivity) mContext).startActivityForResult(intent, 101);
    }

    public void setData(ArrayList<EventBean> eventBeen) {
        mDataFromDB = eventBeen;
        parseData();
        notifyDataSetChanged();
    }

    class ChildViewHolder {
        TextView eventTitle;
        CheckBox eventFinish;
        View eventPriority;
    }

    class GroupViewHolder {
        TextView channelTitle;
        ImageView groupExpand;
    }
}
