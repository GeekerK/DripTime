package com.geekerk.driptime.vo;

/**
 * Created with Android Studio IDEA
 * Author：LiYan
 * Date：2016/5/13
 * Time：11:51
 */
public final class NavBean {
    private int mIconResource;
    private int mNavNameResource;
    private int mMsgNum;

    public NavBean(int iconResource, int navNameResource, int msgNum) {
        mIconResource = iconResource;
        mNavNameResource = navNameResource;
        mMsgNum = msgNum;
    }

    public int getmIconResource() {
        return mIconResource;
    }

    public void setmIconResource(int mIconResource) {
        this.mIconResource = mIconResource;
    }

    public int getNavNameResource() {
        return mNavNameResource;
    }

    public void setNavNameResource(int mNavNameResource) {
        this.mNavNameResource = mNavNameResource;
    }

    public int getmMsgNum() {
        return mMsgNum;
    }

    public void setmMsgNum(int mMsgNum) {
        this.mMsgNum = mMsgNum;
    }

    @Override
    public String toString() {
        return "NavBean{" +
                "mIconResource=" + mIconResource +
                ", mNavNameResource='" + mNavNameResource + '\'' +
                ", mMsgNum=" + mMsgNum +
                '}';
    }
}
