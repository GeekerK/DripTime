package com.geekerk.driptime.fragment;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DataChangeListener;
import com.geekerk.driptime.db.EventDao;
import com.geekerk.driptime.db.ListDao;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by s21v on 2016/5/24.
 */
public class ClosedListFragment extends ListFragment implements DataChangeListener {
    private static final String TAG = "CollapseToolBarFragment";

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
                        int userId = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE).getInt("currentUserID", -1);
                        //删除清单下的事件
                        EventDao eventDao = new EventDao(mDatabaseHelper.getEventDao());
                        eventDao.deleteByUserIdAndListId(userId, mCurrentList.getId());
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
}
