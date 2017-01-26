package com.geekerk.driptime.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Scroller;

import com.geekerk.driptime.R;

/**
 * 在给定的内容布局右侧添加三个按钮
 * Created by s21v on 2016/5/26.
 */
public class LinearLayoutWithAction extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "LinearLayoutWithAction";
    private static final int STATUS_FULL_VISIBLE = 2;
    private static final int STATUS_HIDE = 0;
    private static final int STATUS_MOVE = 1;
    private View contentView;
    private ImageView moveIv, editIv, deleteIv; //三个按钮的宽高等于布局的高度
    private CheckBox isDoneCheck;
    private int imageViewWidth; //按钮的宽度
    private int layoutEventHeight;  //布局的高度（也是按钮的高度）
    private int scrollDistance;
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private float lastX;
    private int defX;
    private int currentStatus = STATUS_HIDE;
    private EventDealInterface eventDealInterface;

    public LinearLayoutWithAction(Context context, int contentRes, int layoutHeightDimen) {
        this(context, null, 0);
        contentView = LayoutInflater.from(context).inflate(contentRes, null);
        contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        isDoneCheck = (CheckBox) contentView.findViewById(R.id.isDone_checkbox);
        isDoneCheck.setOnClickListener(this);

        moveIv = new ImageView(context);
        moveIv.setId(R.id.moveButton);
        moveIv.setImageResource(R.mipmap.today_icon_move);
        moveIv.setScaleType(ImageView.ScaleType.CENTER);
        moveIv.setOnClickListener(this);

        editIv = new ImageView(context);
        editIv.setId(R.id.modifyButton);
        editIv.setImageResource(R.mipmap.today_icon_modify);
        editIv.setScaleType(ImageView.ScaleType.CENTER);
        editIv.setOnClickListener(this);

        deleteIv = new ImageView(context);
        deleteIv.setId(R.id.deleteButton);
        deleteIv.setImageResource(R.mipmap.today_icon_delete);
        deleteIv.setScaleType(ImageView.ScaleType.CENTER);
        deleteIv.setOnClickListener(this);

        addView(contentView);
        addView(moveIv);
        addView(editIv);
        addView(deleteIv);

        imageViewWidth = context.getResources().getDimensionPixelSize(R.dimen.layout_event_height);
        layoutEventHeight = context.getResources().getDimensionPixelSize(layoutHeightDimen);
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
                MeasureSpec.makeMeasureSpec(layoutEventHeight, MeasureSpec.EXACTLY));
        measureChild(moveIv, imageViewWidth, layoutEventHeight);
        measureChild(editIv, imageViewWidth, layoutEventHeight);
        measureChild(deleteIv, imageViewWidth, layoutEventHeight);
        scrollDistance = imageViewWidth * 3;
        setMeasuredDimension(contentView.getMeasuredWidth() + scrollDistance, layoutEventHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(0, 0, contentView.getMeasuredWidth(), layoutEventHeight);
        for (int i = 1; i < getChildCount(); i++)
            getChildAt(i).layout(contentView.getMeasuredWidth() + (i - 1) * imageViewWidth, 0,
                    contentView.getMeasuredWidth() + i * imageViewWidth, layoutEventHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                defX = (int) (event.getX() - lastX);
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
                if (defX < 0) {
                    if (getScrollX() > imageViewWidth)   //左移过1/3即认为是开启操作
                    {
                        scroller.startScroll(getScrollX(), 0, scrollDistance - getScrollX(), 0);
                        currentStatus = STATUS_FULL_VISIBLE;
                    } else {    //左移未过1/3 认为是用户的误操作
                        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                        currentStatus = STATUS_HIDE;
                    }
                } else {
                    if (getScrollX() > imageViewWidth * 2) {  //右移未过1/3 认为是误操作
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

    public void setEventDealInterface(EventDealInterface eventDealInterface) {
        this.eventDealInterface = eventDealInterface;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moveButton:
                eventDealInterface.moveEventAtPosition(this);
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                invalidate();
                break;
            case R.id.modifyButton:
                eventDealInterface.modifyEventAtPosition(this);
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                invalidate();
                break;
            case R.id.deleteButton:
                eventDealInterface.deleteEventAtPosition(this);
                break;
            case R.id.isDone_checkbox:
                eventDealInterface.checkFinish(this);
                break;
        }
    }

    public interface EventDealInterface {
        void checkFinish(LinearLayoutWithAction view);

        void moveEventAtPosition(LinearLayoutWithAction view);

        void deleteEventAtPosition(LinearLayoutWithAction view);

        void modifyEventAtPosition(LinearLayoutWithAction view);
    }
}
