package com.geekerk.driptime.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.geekerk.driptime.R;


/**
 * Created by Administrator on 2016/4/28.
 */
public class DragLayout extends ViewGroup {
    private static final String TAG = "DragLinearLayout";
    private ViewGroup middleLL, leftLL, rightLL; //中间，左边，右边的试图
    private int picResInLeftLayout, picResInRightLayout;    //左视图，右视图的原始图片
    private int replacePicResInLeftLayout, replacePicResInRightLayout;   //左视图，右视图的替换图片
    private int layoutWidth;    //组件的宽度，因为父组件是屏幕窗口，且match_parent所以等于屏幕的宽度
    private int scaledTouchSlop;
    private VelocityTracker velocityTracker;
    private Scroller scroller;

    private enum STATUS {   //这里的参考系是手指滑动方向
        LeftNotHalf, LeftPassHalf, Middle, RightPassHalf, RightNotHalf;
        @Override
        public String toString() {
            switch (name()) {
                case "LeftNotHalf":
                    return "左移未过半";
                case "LeftPassHalf":
                    return "左移过半";
                case "Middle":
                    return "居中";
                case "RightNotHalf":
                    return "右移未过半";
                case "RightPassHalf":
                    return "右移过半";
                default:
                    return super.toString();
            }
        }
    }
    private Enum<STATUS> currentStatus;

    public DragLayout(Context context) {
        this(context, null ,0);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.DragLinearLayout);
        int leftLayoutRes = typeArray.getResourceId(R.styleable.DragLinearLayout_leftLayout, R.layout.left_event_ll);
        int rightLayoutRes = typeArray.getResourceId(R.styleable.DragLinearLayout_rightLayout, R.layout.right_event_ll);
        int middleLayoutRes = typeArray.getResourceId(R.styleable.DragLinearLayout_middleLayout, R.layout.middle_event_ll);
        picResInLeftLayout = typeArray.getResourceId(R.styleable.DragLinearLayout_leftLayoutPic, R.drawable.ic_menu_camera);
        picResInRightLayout = typeArray.getResourceId(R.styleable.DragLinearLayout_rightLayoutPic, R.drawable.ic_menu_gallery);
        replacePicResInLeftLayout = typeArray.getResourceId(R.styleable.DragLinearLayout_leftLayoutReplacePic, R.drawable.ic_menu_manage);
        replacePicResInRightLayout = typeArray.getResourceId(R.styleable.DragLinearLayout_rightLayoutReplacePic, R.drawable.ic_menu_send);
        leftLL = (ViewGroup) LayoutInflater.from(context).inflate(leftLayoutRes, this, false);
        middleLL = (ViewGroup) LayoutInflater.from(context).inflate(middleLayoutRes, this, false);
        rightLL = (ViewGroup) LayoutInflater.from(context).inflate(rightLayoutRes, this, false);
        addView(leftLL);
        addView(middleLL);
        addView(rightLL);
        View view = middleLL.findViewById(R.id.eventTitle);
        Log.i(TAG,"clickable:"+view.isClickable()+" , longClickable："+view.isLongClickable()+" , contextClick:"+view.isContextClickable());
    }

    private void init(Context context) {
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();          //有效滑动距离
        scroller = new Scroller(context, new AccelerateInterpolator());
        //设置初始状态
        currentStatus = STATUS.Middle;
        //设置速度检测
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY);
        //左右布局的高度应与中间的高度一致，因此先计算中间布局的高度，并用它的高度给左右布局使用
        int layoutHeight;
        measureChild(middleLL, childWidthSpec, heightMeasureSpec);
        layoutHeight = middleLL.getMeasuredHeight();
        int leftRightChildHeightSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY);
        measureChild(leftLL, childWidthSpec, leftRightChildHeightSpec);
        measureChild(rightLL, childWidthSpec, leftRightChildHeightSpec);
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=0; i<getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(layoutWidth * (i-1), 0, layoutWidth * i, getMeasuredHeight());
        }
    }

    // -------------------------- 滑动相关 -------------------------
    float lastX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                Log.i(TAG, "onInterceptTouchEvent ACTION_DOWN");
                lastX = ev.getX();
                velocityTracker.clear();
                velocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE :  //阻止checkbox获得Move事件
                Log.i(TAG, "onInterceptTouchEvent ACTION_MOVE");
                float defX = ev.getX() - lastX;
                velocityTracker.addMovement(ev);
                velocityTracker.computeCurrentVelocity(1000);
                if (Math.abs(defX) >= scaledTouchSlop && Math.abs(velocityTracker.getXVelocity()) > Math.abs(velocityTracker.getYVelocity()))
                    return true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :  //TextView不处理Down事件所以会将Down事件交给其上的组件处理，直到交给本组件处理，返回true来接管整个事件队列
                Log.i(TAG, "onTouchEvent ACTION_DOWN");
                lastX = event.getX();
                velocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent ACTION_MOVE");
                float defX = event.getX() - lastX;
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                //通过 X Y轴上速度的对比来判断是否是组件滑动
                if (Math.abs(defX) >= scaledTouchSlop && Math.abs(velocityTracker.getXVelocity()) > Math.abs(velocityTracker.getYVelocity())) {
                    if (getScrollX() <= -layoutWidth && defX > 0)    //右移过头了
                        return false;
                    else if (getScrollX() >= layoutWidth && defX < 0)   //左移过头了
                        return false;
                    //组件滑动过程中阻止父空间拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (getScrollX() - defX < -layoutWidth)  //右移刚过头
                        scrollTo(-layoutWidth, 0);
                    else if (getScrollX() - defX > layoutWidth) //左移刚过头
                        scrollTo(layoutWidth, 0);
                    else {
                        scrollBy((int) -defX, 0);
                        if (getScrollX() < -layoutWidth / 2) { //右移过半
                            //改变左视图
                            leftLL.setBackgroundColor(Color.BLUE);
                            ((ImageView) leftLL.getChildAt(0)).setImageResource(replacePicResInLeftLayout);
                            //改变状态
                            currentStatus = STATUS.RightPassHalf;
                        } else if (getScrollX() > -layoutWidth / 2 && getScrollX() < -layoutWidth / 6) { //右移未过半
                            //改变左视图
                            leftLL.setBackgroundColor(Color.YELLOW);
                            ((ImageView) leftLL.getChildAt(0)).setImageResource(picResInLeftLayout);
                            //改变状态
                            currentStatus = STATUS.RightNotHalf;
                        } else if (getScrollX() > layoutWidth / 2) {   //左移过半
                            //改变右视图
                            rightLL.setBackgroundColor(Color.RED);
                            ((ImageView) rightLL.getChildAt(0)).setImageResource(replacePicResInRightLayout);
                            //改变状态
                            currentStatus = STATUS.LeftPassHalf;
                        } else if (getScrollX() > layoutWidth / 6 && getScrollX() < layoutWidth / 2) {   //左移未过半
                            //改变右视图
                            rightLL.setBackgroundColor(Color.GREEN);
                            ((ImageView) rightLL.getChildAt(0)).setImageResource(picResInRightLayout);
                            //改变状态
                            currentStatus = STATUS.LeftNotHalf;
                        } else {
                            currentStatus = STATUS.Middle;
                            leftLL.setBackgroundColor(Color.WHITE);
                            rightLL.setBackgroundColor(Color.WHITE);
                        }
                    }
                    lastX = event.getX();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP :
                Log.i(TAG, "onTouchEvent ACTION_UP : Status:"+currentStatus);
                if (currentStatus == STATUS.LeftNotHalf || currentStatus == STATUS.LeftPassHalf) {
                    scroller.startScroll(getScrollX(), 0 , layoutWidth -getScrollX(), 0);
                    //组件滑动过程中阻止父空间拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if (currentStatus == STATUS.RightPassHalf || currentStatus == STATUS.RightNotHalf) {
                    scroller.startScroll(getScrollX(), 0 , -(layoutWidth +getScrollX()), 0);
                    //组件滑动过程中阻止父空间拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if(currentStatus == STATUS.Middle) {
                    scroller.startScroll(getScrollX(), 0 , -getScrollX(), 0);
                    //组件滑动过程中阻止父空间拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(l == -layoutWidth) { //右移事件结束
            if(currentStatus == STATUS.RightNotHalf) {
//                Log.i(TAG, "左视图1的事件");
                if (mEventListener!=null)
                    mEventListener.onRightNotHalfEvent();
            } else if (currentStatus == STATUS.RightPassHalf) {
//                Log.i(TAG, "左视图2的事件");
                if (mEventListener!=null)
                    mEventListener.onRightPassHalfEvent();
            }
           getParent().requestDisallowInterceptTouchEvent(false);
        }
        if(l == layoutWidth) {   //左移事件结束
            if (currentStatus == STATUS.LeftNotHalf) {
//                Log.i(TAG, "右视图1的事件");
                if (mEventListener!=null)
                    mEventListener.onLeftNotHalfEvent();
            } else if (currentStatus == STATUS.LeftPassHalf) {
//                Log.i(TAG, "右视图2的事件");
                if (mEventListener!=null)
                    mEventListener.onLeftPassHalfEvent();
            }
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        if (l == 0) {   //视图复位
            currentStatus = STATUS.Middle;
            leftLL.setBackgroundColor(Color.WHITE);
            rightLL.setBackgroundColor(Color.WHITE);
            ((ImageView)leftLL.getChildAt(0)).setImageResource(picResInLeftLayout);
            ((ImageView)rightLL.getChildAt(0)).setImageResource(picResInRightLayout);
            getParent().requestDisallowInterceptTouchEvent(false);
        }
    }

    //视图复位
    public void resetLayout() {
        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }
    // --------------------- 滑动相关 end ---------------------

    // ---------------------- 定义接口 -------------------------
    public interface EventListener {
        void onLeftNotHalfEvent();      //手指左移未到一半
        void onLeftPassHalfEvent();     //手指左移过半
        void onRightNotHalfEvent();     //手指右移未到一半
        void onRightPassHalfEvent();    //手指右移过半
    }

    private EventListener mEventListener;

    public void setEventListener(EventListener mEventListener) {
        this.mEventListener = mEventListener;
    }
    // ----------------------- 定义接口 end -----------------------

    // ----------------------- 外部接口 ---------------------
    public void setPriorityColor(int priority) {    //设置优先级颜色
        ImageView imageView = (ImageView) middleLL.findViewById(R.id.priority_iv);
        int color = Color.WHITE;
        switch (priority) {
            case 0 :
                color = Color.WHITE;
                break;
            case 1 :
                color = Color.parseColor("#FBC02D");
                break;
            case 2 :
                color = Color.parseColor("#F57C00");
                break;
            case 3 :
                color = Color.parseColor("#D32F2F");
                break;
        }
        imageView.setBackgroundColor(color);
    }

    public void setEventTitle(String title) { //设置事件标题
        TextView textView = (TextView) middleLL.findViewById(R.id.eventTitle);
        textView.setText(title);
    }
    // ----------------------- 外部接口 end---------------------
}
