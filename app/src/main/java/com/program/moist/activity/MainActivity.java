package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.program.moist.R;
import com.program.moist.adapters.FragPageAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityMainBinding;
import com.program.moist.fragment.AddFragment;
import com.program.moist.fragment.DiscoverFragment;
import com.program.moist.fragment.HomeFragment;
import com.program.moist.fragment.MessageFragment;
import com.program.moist.fragment.UserFragment;
import com.program.moist.utils.FTPUtil;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.program.moist.base.AppConst.TAG;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding activityMainBinding;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(activityMainBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {

        //初始化底部导航
        activityMainBinding.bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setBarBackgroundColor(R.color.background_white)
                .addItem(new BottomNavigationItem(R.drawable.ic_home_fill, R.string.home))
                .addItem(new BottomNavigationItem(R.drawable.ic_discover_fill, R.string.explore))
                .addItem(new BottomNavigationItem(R.drawable.ic_add_fill, "添加"))
                .addItem(new BottomNavigationItem(R.drawable.ic_message_fill, R.string.message))
                .addItem(new BottomNavigationItem(R.drawable.ic_user_fill, R.string.me))
                .setFirstSelectedPosition(0)
                .setActiveColor(R.color.green_3)
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        Log.i("tab isssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss " + position, "onTab");
                        switch (position) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                activityMainBinding.mainViewPager.setCurrentItem(position);
                                break;
                            case 4:
                                Log.i("shitttttttttttttttttttttttttttttttttt", "onTabSelected: ");
                                String login_token = SharedUtil.getString(App.context, AppConst.Base.login_token, "");
                                if (login_token.equals("")) startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                else activityMainBinding.mainViewPager.setCurrentItem(position, true);
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(int position) {

                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                })
                .initialise();
        Log.i(TAG, "initView: bottom bar initialed");

        //初始化viewpager2
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance());
        fragments.add(DiscoverFragment.newInstance(TAG, TAG));
        fragments.add(AddFragment.newInstance(TAG, TAG));
        fragments.add(MessageFragment.newInstance(TAG, TAG));
        fragments.add(UserFragment.newInstance());
        activityMainBinding.mainViewPager.setAdapter(new FragPageAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        activityMainBinding.mainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                activityMainBinding.bottomNavigationBar.selectTab(position);
            }
        });
        activityMainBinding.mainViewPager.setCurrentItem(0);
        Log.i(TAG, "initView: viewpager2 initialed");
    }

    @Override
    protected void eventBind() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToastShort(this, "再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                App.exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class TabClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

        }
    }
}