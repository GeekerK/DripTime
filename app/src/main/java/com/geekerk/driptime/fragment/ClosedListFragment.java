package com.geekerk.driptime.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.geekerk.driptime.R;
import com.geekerk.driptime.adapter.DataChangeListener;
import com.geekerk.driptime.adapter.EventRecyclerViewAdapter;
import com.geekerk.driptime.db.ListDao;
import com.geekerk.driptime.view.LinearLayoutWithAction;
import com.geekerk.driptime.vo.ListBean;

import java.sql.SQLException;

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

        }
        return super.onOptionsItemSelected(item);
    }
}
