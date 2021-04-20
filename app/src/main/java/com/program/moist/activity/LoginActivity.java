package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.program.moist.R;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityLoginBinding;

/**
 * Author: SilentSherlock
 * Date: 2021/4/15
 * Description: describe the class
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(activityLoginBinding.getRoot());
        activityLoginBinding.loginToRegister.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.login_to_register:
                intent.setClass(this, RegisterActivity.class);
                break;
        }
        startActivity(intent);
    }
}
