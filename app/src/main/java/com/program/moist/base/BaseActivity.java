package com.program.moist.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Author: SilentSherlock
 * Date: 2021/4/19
 * Description: describe the class
 */
public abstract class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!App.activities.contains(this)) {
            App.activities.add(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.activities.remove(this);
    }

    protected abstract void initView();

    protected abstract void eventBind();
}
