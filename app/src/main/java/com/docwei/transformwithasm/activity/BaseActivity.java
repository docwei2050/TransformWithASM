package com.docwei.transformwithasm.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.docwei.transformwithasm.application.MyApplication;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void protectApp() {
        Intent intent = MyApplication.getInstance().getPackageManager().getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getInstance().startActivity(intent);
            overridePendingTransition(0, 0);
        }

    }
}
