package com.geekerk.driptime.fragment;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DataChangeListener;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import com.j256.ormlite.misc.TransactionManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by s21v on 2016/5/24.
 */
public class ClosedListFragment extends ListFragment implements DataChangeListener {
    private static final String TAG = "CollapseToolBarFragment";

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_closed_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_list_open) {    //回复关闭的清单
            if (mCurrentList != null) {
                try {
                    ListDao listDao = new ListDao(mDatabaseHelper.getListDao());
                    mCurrentList.setClosed(false);
                    listDao.update(mCurrentList);
                    Toast.makeText(getContext(), String.format(getResources().getString(R.string.listopen_success), mToolbarTitle), Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else if (id == R.id.menu_list_delete) {    //删除清单
            boolean result = false;
            try {
                result = TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(), new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        //删除清单下的事件
                        EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
//                        eventDao.deleteByUserIdAndListId(mCurrentList.getUser().getId(), mCurrentList.getId());
                        ArrayList<EventBean> tmp = new ArrayList<EventBean>();
                        tmp.addAll(eventDao.queryByUserIdAndListId(mCurrentList.getUser().getId(), mCurrentList.getId()));
                        eventDao.deleteSet(tmp);
                        //删除清单
                        ListDao listDao = new ListDao(mDatabaseHelper.getListDao());
                        listDao.delete(mCurrentList);
                        return true;
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (result) {
                //更新activity
                listChangeListener.listDelete();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCurrentListBean() {
        if (mCurrentList == null) {
            mCurrentList = new ListBean(Integer.parseInt(mQueryArgs[1]));
            mCurrentList.setName(mToolbarTitle);
            mCurrentList.setUser(new UserBean(Integer.parseInt(mQueryArgs[0])));
        } else {
            mCurrentList.setId(Integer.parseInt(mQueryArgs[1]));
            mCurrentList.setName(mToolbarTitle);
            mCurrentList.setUser(new UserBean(Integer.parseInt(mQueryArgs[0])));
        }
        mCurrentList.setClosed(false);
    }
}
