package com.geekerk.driptime.vo;

/**
 * Created with Android Studio IDEA
 * Author：LiYan
 * Date：2016/5/13
 * Time：11:51
 */
public final class NavBean {
    private int mIconResource;
    private String mNavName;
    private int mMsgNum;

    public NavBean(int iconResource, String navName, int msgNum) {
        mIconResource = iconResource;
        mNavName = navName;
        mMsgNum = msgNum;
    }

    public int getmIconResource() {
        return mIconResource;
    }

    public void setmIconResource(int mIconResource) {
        this.mIconResource = mIconResource;
    }

    public String getmNavName() {
        return mNavName;
    }

    public void setmNavName(String mNavName) {
        this.mNavName = mNavName;
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
                ", mNavName='" + mNavName + '\'' +
                ", mMsgNum=" + mMsgNum +
                '}';
    }
}
