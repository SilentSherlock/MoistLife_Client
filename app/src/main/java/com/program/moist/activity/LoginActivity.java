package com.program.moist.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.program.moist.databinding.ActivityLoginBinding;

/**
 * Author: SilentSherlock
 * Date: 2021/4/15
 * Description: describe the class
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(activityLoginBinding.getRoot());
    }
}
