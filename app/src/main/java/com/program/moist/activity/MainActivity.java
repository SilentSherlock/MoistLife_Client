package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.program.moist.R;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(activityMainBinding.getRoot());
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Intent intent = new Intent();
        System.out.println(view == null);
        switch (view.getId()) {
            case R.id.main_to_login:
                intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
    }
}