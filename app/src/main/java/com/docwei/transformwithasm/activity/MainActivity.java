package com.docwei.transformwithasm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.docwei.transformwithasm.AppStatusManager;
import com.docwei.transformwithasm.R;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void show(View view) {
        Intent intent = new Intent(MainActivity.this, SecondAct.class);
        startActivity(intent);

    }

}














