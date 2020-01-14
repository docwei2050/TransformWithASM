package com.docwei.transformwithasm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.docwei.transformwithasm.R;


public class SecondAct extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);

    }
    public void show(View view){
        Intent intent = new Intent(SecondAct.this, ThirdAct.class);
        startActivity(intent);
    }
}
