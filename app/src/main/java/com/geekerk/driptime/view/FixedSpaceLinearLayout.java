package com.geekerk.driptime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import com.geekerk.driptime.R;
import static android.widget.LinearLayout.LayoutParams.*;

/**
 * Created by s21v on 2016/5/25.
 */
public class FixedSpaceLinearLayout extends LinearLayout {
    private int headerBgImageId;    //headerView 中的背景图片
    private String headerBgText;    //headerView 中的文字
    private View headerView;
    private int headerBarHeight;    //headerBar 的高度，当拖动到headerbar时就不应该拖动
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int touchSlop;
    private int minimumFlingVelocity;
    private int scrollDistance;
    private int screenHeight;

    public FixedSpaceLinearLayout(Context context) {
        this(context, null, 0);
    }

    public FixedSpaceLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedSpaceLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedSpaceLinearLayout);
        headerBgImageId = typedArray.getResourceId(R.styleable.FixedSpaceLinearLayout_headerBgImageId, R.mipmap.bj_today);
        headerBgText = typedArray.getString(R.styleable.FixedSpaceLinearLayout_headerBgText);
        typedArray.recycle();

        //添加子视图
        headerView = LayoutInflater.from(context).inflate(R.layout.header_bg, null);
        headerView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        addView(headerView);
        contentList = new ListView(context);
        addView(contentList);

        //获得操作栏的高度
        headerBarHeight = getResources().getDimensionPixelSize(R.dimen.header_action_bar_height);

        //获得屏幕高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;

        //滑动相关
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        minimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int headerViewHeight = BitmapFactory.decodeResource(getResources(), headerBgImageId).getHeight();
        headerView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(headerViewHeight, MeasureSpec.EXACTLY));
        contentList.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec)-headerBarHeight, MeasureSpec.EXACTLY));
        scrollDistance = headerViewHeight - headerBarHeight;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    //列表内容，具体View可自定义，这里用ListView做实验
    private ListView contentList;

    public ListView getContentList() {
        return contentList;
    }

    private static final int STATUS_FULL = 1;
    private static final int STATUS_EMPTY = 0;
    private static final int STATUS_MOVE = 2;
    private int currentStatus = STATUS_FULL;

    private float lastY;

    private static final String TAG = "FixedSpaceLinearLayout";
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onInterceptTouchEvent ACTION_DOWN");
                lastY = ev.getY();
                velocityTracker.clear();
                velocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onInterceptTouchEvent ACTION_MOVE");
                float defY = ev.getY() - lastY;
                velocityTracker.addMovement(ev);
                velocityTracker.computeCurrentVelocity(1000);
                if (Math.abs(defY)>touchSlop && Math.abs(velocityTracker.getYVelocity())>Math.abs(velocityTracker.getXVelocity())){
                    if (currentStatus == STATUS_FULL) { //header完全显示
                        if(defY>0)  //手指向下移动
                            return false;
                        else    //手指向上移动
                            return true;
                    } else if (currentStatus == STATUS_EMPTY) { //header折叠
                        if(defY>0 && contentList.getFirstVisiblePosition()==0)
                            return true;
                        else
                            return false;
                    } else if (currentStatus == STATUS_MOVE) {
                        return true;
                    }
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                Log.i(TAG, "onTouchEvent ACTION_DOWN");
                lastY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE :
                Log.i(TAG, "onTouchEvent ACTION_MOVE scrollY:"+getScrollY()+", scrollDistance:"+scrollDistance+", status: "+currentStatus);
                float defY = event.getY() - lastY;
                velocityTracker.computeCurrentVelocity(1000);
                if (Math.abs(defY)>touchSlop && Math.abs(velocityTracker.getYVelocity())>Math.abs(velocityTracker.getXVelocity())) {
                    if (currentStatus == STATUS_FULL && defY<0) {   //Header完全显示且向上滑动
                        Log.i(TAG, "onTouchEvent ACTION_MOVE Header完全显示且向上滑动");
                        if (getScrollY()-defY > scrollDistance) { //上移过头
                            Log.i(TAG, "onTouchEvent ACTION_MOVE 上移过头!!!!!!!!");
                            scrollTo(0, scrollDistance);
                            currentStatus = STATUS_EMPTY;
                        } else {
                            scrollBy(0, (int) -defY);
                            currentStatus = STATUS_MOVE;
                        }
                    } else if(currentStatus == STATUS_MOVE) {
                        if (defY<0) {   //向上滑动
                            if (getScrollY()-defY > scrollDistance) { //上移过头
                                scrollTo(0, scrollDistance);
                                currentStatus = STATUS_EMPTY;
                            } else
                                scrollBy(0, (int) -defY);
                        } else {    //向下滑动
                            if (getScrollY()-defY < 0) {   //下移过头
                                scrollTo(0, 0);
                                currentStatus = STATUS_FULL;
                            } else {
                                scrollBy(0, (int) -defY);
                                currentStatus = STATUS_MOVE;
                            }
                        }
                    } else if (currentStatus == STATUS_EMPTY && defY>0 && contentList.getFirstVisiblePosition()==0) {
                        //Header不显示，列表已在第一项，且还向下移动
                        if (getScrollY()-defY < 0) {   //下移过头
                            scrollTo(0, 0);
                            currentStatus = STATUS_FULL;
                        } else {
                            scrollBy(0, (int) -defY);
                            currentStatus = STATUS_MOVE;
                        }
                    }
                    lastY = event.getY();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                velocityTracker.computeCurrentVelocity(1000);
                float velocityY = velocityTracker.getYVelocity();
                Log.i(TAG, "onTouchEvent ACTION_UP velocityY: "+velocityY);
                if (Math.abs(velocityY) > 1){//minimumFlingVelocity) {
                    if (velocityY<0 && currentStatus!=STATUS_EMPTY) {
                        Log.i(TAG, "onTouchEvent ACTION_UP Fling up ScrollY():"+getScrollY()+" ,dy:"+(scrollDistance-getScrollY()));
                        scroller.startScroll(0, getScrollY(), 0, scrollDistance-getScrollY());
                        currentStatus = STATUS_EMPTY;
                    } else if (velocityY>0 && currentStatus!=STATUS_FULL) {
                        Log.i(TAG, "onTouchEvent ACTION_UP Fling down");
                        scroller.startScroll(0, getScrollY(), 0, -getScrollY());
                        currentStatus = STATUS_FULL;
                    }
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.getCurrY());
            postInvalidate();
        }
    }
}
