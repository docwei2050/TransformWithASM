package com.docwei.transformwithasm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.docwei.transformwithasm.AppStatusManager;
import com.docwei.transformwithasm.R;


public class SplashAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Splash设置app的状态
        AppStatusManager.getInstance().setAppStatus(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    public void show(View view) {
        Intent intent=new Intent(SplashAct.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
