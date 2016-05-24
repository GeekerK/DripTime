package com.geekerk.driptime.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.geekerk.driptime.R;

/**
 * Created by s21v on 2016/5/24.
 */
public class ListviewWithImageHeader extends ListView {
    private static final String TAG = "ListviewWithImageHeader";
    private View headerView;
    private int headerViewHeight;
    private int headerBarHeight;

    public ListviewWithImageHeader(Context context) {
        super(context);
        init(context);
    }

    public ListviewWithImageHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListviewWithImageHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        headerView = LayoutInflater.from(context).inflate(R.layout.header_bg, null);
        headerViewHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        addHeaderView(headerView);
        headerBarHeight = getResources().getDimensionPixelSize(R.dimen.header_action_bar_height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        headerView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(headerViewHeight, MeasureSpec.EXACTLY));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
