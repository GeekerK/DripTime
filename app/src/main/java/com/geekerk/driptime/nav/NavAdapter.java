package com.geekerk.driptime.nav;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.geekerk.driptime.R;
import com.geekerk.driptime.db.DataBaseHelper;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.utils.LayoutUtil;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.NavBean;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio IDEA
 * Author：LiYan
 * Date：2016/5/12
 * Time：14:52
 */
public final class NavAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "NavAdapter";
    private Context mContext;
    private List<NavBean> mGroups;
    private List<ListBean> mLists, mClosedLists;
    private ininitData();
    }

    public NavAdapter(Context context, List<NavBean> groups) {
        mContext = context;
        mGroups = groups;
        mCurrentUserId = context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("currentUserID",-1);

t mCurrentUserId;

    public void setmGroups(List<NavBean> mGroups) {
        this.mGroups = mGroups;

initData();
    }

    //更新清单数据
    public void initData() {
        DataBaseHelper helper = OpenHelperManager.getHelper(mContext, DataBaseHelper.class);
        try {
            ListDao listDao = new ListDao(helper.getListDao());
            mLists = listDao.queryCustomList(mCurrentUserId,false);
            mClosedLists = listDao.queryCustomList(mCurrentUserId,true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            OpenHelperManager.releaseHelper();
            helper = null;
            if (mLists == null)
                mLists = new ArrayList<>();
            if (mClosedLists == null)
                mClosedLists = new ArrayList<>();
            //设置清单列表 和 已关闭清单列表 的数量
            mGroups.get(6).setmMsgNum(mLists.size());
            mGroups.get(7).setmMsgNum(mClosedLists.size());
        }
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
        ItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nav_groups, null);
            convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutUtil.getPixelByDIP(mContext, 60)));
            viewHolder = new ItemViewHolder();
            viewHolder.navTitleIv = (TextView) convertView.findViewById(R.id.nav_groups_name);
            viewHolder.navIconIv = (ImageView) convertView.findViewById(R.id.nav_groups_icon);
            viewHolder.navMsgView = convertView.findViewById(R.id.nav_groups_msg);
            viewHolder.navMsgNumTv = (TextView) convertView.findViewById(R.id.nav_msg_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }
        viewHolder.navTitleIv.setText(mGroups.get(groupPosition).getNavNameResource());
        viewHolder.navIconIv.setImageResource(mGroups.get(groupPosition).getmIconResource());
        viewHolder.navMsgNumTv.setText(String.valueOf(mGroups.get(groupPosition).getmMsgNum()));
        viewHolder.navMsgView.setVisibility(mGroups.get(groupPosition).getmMsgNum() == 0 ? View.INVISIBLE : View.VISIBLE);
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        NavBean currentGroup = mGroups.get(groupPosition);
        if (currentGroup.getNavNameResource() == R.string.lists) {
            if (childPosition == mLists.size()) //这里是添加清单，没有对应的Object
                return null;
            else
                return mLists.get(childPosition);
        } else if (currentGroup.getNavNameResource() == R.string.closed_lists) {
            return mClosedLists.get(childPosition);
        }
        return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        NavBean currentGroup = mGroups.get(groupPosition);
        if (currentGroup.getNavNameResource() == R.string.lists) {
            return mLists.size()+1; //清单列表中有一固定项 添加清单 ，所以这里返回Count要加1
        } else if (currentGroup.getNavNameResource() == R.string.closed_lists) {
            return mClosedLists.size();
        }
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nav_groups, null);
            convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutUtil.getPixelByDIP(mContext, 60)));
            convertView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.navChildPaddingleft),0,0,0);
            viewHolder = new ItemViewHolder();
            viewHolder.navTitleIv = (TextView) convertView.findViewById(R.id.nav_groups_name);
            viewHolder.navIconIv = (ImageView) convertView.findViewById(R.id.nav_groups_icon);
            viewHolder.navMsgView = convertView.findViewById(R.id.nav_groups_msg);
            viewHolder.navMsgNumTv = (TextView) convertView.findViewById(R.id.nav_msg_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }
        NavBean currentGroup = mGroups.get(groupPosition);
        if (currentGroup.getNavNameResource() == R.string.lists) {   //清单列表中的
            if (isLastChild) {
                viewHolder.navTitleIv.setText(mContext.getResources().getText(R.string.addList));
                viewHolder.navIconIv.setImageResource(R.mipmap.nav_additem);
                viewHolder.navMsgView.setVisibility(View.GONE);
            } else {
                viewHolder.navTitleIv.setText(mLists.get(childPosition).getName());
                viewHolder.navIconIv.setImageResource(R.mipmap.nav_nearlysevendays);
                // TODO: 2016/6/12 设置清单里的事件数
                viewHolder.navMsgView.setVisibility(View.GONE);
            }
        } else if (currentGroup.getNavNameResource() == R.string.closed_lists){   //已关闭清单中的
            viewHolder.navTitleIv.setText(mClosedLists.get(childPosition).getName());
            viewHolder.navIconIv.setImageResource(R.mipmap.nav_nearlysevendays);
            viewHolder.navMsgView.setVisibility(View.GONE);
        }
        return convertView;
    }

    //返回指定childPosition 所表示的清单ID
    public int getListId(int groupPosition, int childPosition) {
        NavBean currentGroup = mGroups.get(groupPosition);
        if (currentGroup.getNavNameResource() == R.string.lists) {   //清单列表中的清单
            return mLists.get(childPosition).getId();
        } else if (currentGroup.getNavNameResource() == R.string.closed_lists){
            return mClosedLists.get(childPosition).getId();
        }
        return 0;
    }

    public String getListName(int groupPosition, int childPosition) {
        NavBean currentGroup = mGroups.get(groupPosition);
        if (currentGroup.getNavNameResource() == R.string.lists) {   //清单列表中的清单
            return mLists.get(childPosition).getName();
        } else if (currentGroup.getNavNameResource() == R.string.closed_lists){
            return mClosedLists.get(childPosition).getName();
        }
        r
avMsgNumTv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
   View n
eturn true;
    }

    @Override
    public boolean hasStableIds() {
        r
eturn true;
    }

    //指定位置是否是清单列表中的最后一个位置,即是否是添加清单的位置
    public boolean isLastChild(int childPosition) {
        return childPosition      r
eturn null;
    }

    class ItemViewHolder {
        TextView navTitleIv;
        ImageView navIconIv;
        View navMsgView;
        Text== mLists.size();
    }
}
