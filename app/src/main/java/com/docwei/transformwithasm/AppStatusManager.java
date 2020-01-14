package com.docwei.transformwithasm;

/**
 * Created by git on 2017/12/16.
 */

public class AppStatusManager {
    public static final int YC_KILLAPP_BYSYSTEM = -1;
    public static final int YC_APP_NORMAL = 0;   //正常状态
    public int mAppStatus = YC_KILLAPP_BYSYSTEM;//被系统杀死或者未进入app是这个状态

    private AppStatusManager() {
    }

    public static AppStatusManager getInstance() {
        return AppStatusManagerInner.instance;
    }

    public int getAppStatus() {
        return mAppStatus;
    }

    public void setAppStatus(int appStatus) {
        mAppStatus = appStatus;
    }

    public static class AppStatusManagerInner {
        static final AppStatusManager instance = new AppStatusManager();
    }

}
