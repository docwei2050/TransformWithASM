package com.docwei.transformwithasm.application;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        try {
            //模拟初始化耗时
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   public static Context getInstance(){
        return instance;
   }

}
