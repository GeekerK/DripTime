package com.geekerk.driptime.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekerk.driptime.R;
import com.geekerk.driptime.view.DragLayout;


/**
 * 测试用 数据是写死的
 * Created by s21v on 2016/4/30.
 */
public class DragRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public DragRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 6)
            return 1;
        else
            return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextViewHolder textViewHolder = new TextViewHolder(view);
            return textViewHolder;
        } else {
            DragLayout dragLayout = (DragLayout) LayoutInflater.from(context).inflate(R.layout.dragll_item, parent, false);
            DragViewHolder dragViewHolder = new DragViewHolder(dragLayout, context);
            return dragViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position==0)
            ((TextViewHolder)holder).setTitle("未完成");
        else if (position == 6)
            ((TextViewHolder)holder).setTitle("已完成");
        else {
            ((DragViewHolder)holder).setEventTitle("事件清单："+position);
            if (position < 6)
                ((DragViewHolder)holder).setPriorityColor((6-position)%4);
            else {
                ((DragViewHolder)holder).setPriorityColor(0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    static class DragViewHolder extends RecyclerView.ViewHolder implements DragLayout.EventListener{
        DragLayout dragLinearLayout;
        private Context context;

        public DragViewHolder(View itemView, Context context) {
            super(itemView);
            dragLinearLayout = (DragLayout) itemView;
            this.context = context;
            dragLinearLayout.setEventListener(this);
        }

        public void setPriorityColor(int priority) {
            dragLinearLayout.setPriorityColor(priority);
        }

        public void setEventTitle(String title) {
            dragLinearLayout.setEventTitle(title);
        }

        @Override
        public void onLeftNotHalfEvent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setCancelable(true).setTitle("左移未过半").setIcon(R.drawable.ic_menu_share)
                    .setMessage("左移未过半,啦啦啦").create();
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dragLinearLayout.resetLayout();
                }
            });
        }

        @Override
        public void onLeftPassHalfEvent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setCancelable(true).setTitle("左移过半").setIcon(R.drawable.ic_menu_camera)
                    .setMessage("左移过半,啦啦啦").create();
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dragLinearLayout.resetLayout();
                }
            });
        }

        @Override
        public void onRightNotHalfEvent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setCancelable(true).setTitle("右移未过半").setIcon(R.drawable.ic_menu_gallery)
                    .setMessage("右移未过半,啦啦啦").create();
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dragLinearLayout.resetLayout();
                }
            });
        }

        @Override
        public void onRightPassHalfEvent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setCancelable(true).setTitle("右移过半").setIcon(R.drawable.ic_menu_manage)
                    .setMessage("右移过半,啦啦啦").create();
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dragLinearLayout.resetLayout();
                }
            });
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }

        public void setTitle(String title) {
            textView.setText(title);
        }
    }
}
