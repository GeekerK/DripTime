package com.geekerk.driptime.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;
import com.geekerk.driptime.R;

/**
 * Created by s21v on 2016/5/26.
 */
public class LinearLayoutWithAction extends ViewGroup {
    private View contentView;
    private ImageView moveIv, editIv, deleteIv;
    private static final String TAG = "LinearLayoutWithAction";
    private int layoutEventHeight;
    private int scrollDistance;
    private int touchSlop;
    private Scroller scroller;
    private VelocityTracker velocityTracker;

    public LinearLayoutWithAction(Context context, int contentRes) {
        this(context, null, 0);
        contentView = LayoutInflater.from(context).inflate(contentRes, null);
        contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        moveIv = new ImageView(context);
        moveIv.setImageResource(R.mipmap.today_icon_move);
        moveIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        editIv = new ImageView(context);
        editIv.setImageResource(R.mipmap.today_icon_modify);
        editIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        deleteIv = new ImageView(context);
        deleteIv.setImageResource(R.mipmap.today_icon_delete);
        deleteIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        addView(contentView);
        addView(moveIv);
        addView(editIv);
        addView(deleteIv);

        layoutEventHeight = context.getResources().getDimensionPixelSize(R.dimen.layout_event_height);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        velocityTracker = VelocityTracker.obtain();
        scroller = new Scroller(context);

    }

    public LinearLayoutWithAction(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutWithAction(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        measureChild(contentView, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(layoutEventHeight,MeasureSpec.EXACTLY));
        measureChild(moveIv, layoutEventHeight, layoutEventHeight);
        measureChild(editIv, layoutEventHeight, layoutEventHeight);
        measureChild(deleteIv, layoutEventHeight, layoutEventHeight);
        scrollDistance = layoutEventHeight*3;
        setMeasuredDimension(contentView.getMeasuredWidth()+scrollDistance, layoutEventHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(0, 0, contentView.getMeasuredWidth(), layoutEventHeight);
        for(int i=1; i<getChildCount(); i++)
            getChildAt(i).layout(contentView.getMeasuredWidth()+(i-1)*layoutEventHeight, 0,
                    contentView.getMeasuredWidth()+i*layoutEventHeight, layoutEventHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        dispatchDraw(canvas);
    }

    private float lastX;
    private static final int STATUS_FULL_VISIBLE = 2;
    private static final int STATUS_HIDE = 0;
    private static final int STATUS_MOVE = 1;
    private int currentStatus = STATUS_HIDE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                Log.i(TAG, "onTouchEvent ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent ACTION_MOVE");
                int defX = (int) (event.getX() - lastX);
                lastX = event.getX();
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                if (Math.abs(velocityTracker.getXVelocity()) > Math.abs(velocityTracker.getYVelocity())) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (currentStatus == STATUS_HIDE) {
                        if (defX < 0)   //完全隐藏且向左滑动
                        {
                            scrollBy(-defX, 0);
                            currentStatus = STATUS_MOVE;
                            return true;
                        } else
                            return false;
                    } else if (currentStatus == STATUS_FULL_VISIBLE) {
                        if (defX > 0)   //完全显示且向右滑动
                        {
                            scrollBy(-defX, 0);
                            currentStatus = STATUS_MOVE;
                            return true;
                        } else
                            return false;
                    } else if (currentStatus == STATUS_MOVE) {
                        if (getScrollX() - defX < 0)  //右移过头
                        {
                            scrollTo(0, 0);
                            currentStatus = STATUS_HIDE;
                        } else if (getScrollX() - defX > scrollDistance)    //左移过头了
                        {
                            scrollTo(scrollDistance, 0);
                            currentStatus = STATUS_FULL_VISIBLE;
                        } else
                            scrollBy(-defX, 0);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent ACTION_UP");
                stopNestedScroll();
                defX = (int) (event.getX() - lastX);
                lastX = event.getX();
                if (defX < 0) {
                    if (getScrollX() > layoutEventHeight)   //左移过1/3即认为是开启操作
                    {
                        scroller.startScroll(getScrollX(), 0, scrollDistance - getScrollX(), 0);
                        currentStatus = STATUS_FULL_VISIBLE;
                    } else {    //左移未过1/3 认为是用户的误操作
                        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                        currentStatus = STATUS_HIDE;
                    }
                } else {
                    if (getScrollX() > layoutEventHeight*2) {  //右移未过1/3 认为是误操作
                        scroller.startScroll(getScrollX(), 0, scrollDistance - getScrollX(), 0);
                        currentStatus = STATUS_FULL_VISIBLE;
                    } else {    //右移过1/3 即认为是关闭操作
                        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                        currentStatus = STATUS_HIDE;
                    }
                }
                invalidate();
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
    return super.onTouchEvent(event);
}

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }
}
