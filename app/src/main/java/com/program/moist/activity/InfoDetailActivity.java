package com.program.moist.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.program.moist.adapters.BannerImageAdapter;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityInfoDetailBinding;
import com.program.moist.entity.Information;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: SilentSherlock
 * Date: 2021/5/7
 * Description: describe the class
 */
public class InfoDetailActivity extends BaseActivity {

    private ActivityInfoDetailBinding activityInfoDetailBinding;
    private BannerImageAdapter bannerImageAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInfoDetailBinding = ActivityInfoDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(activityInfoDetailBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        Information information = (Information) getIntent().getSerializableExtra("information");
        //String[] images = information.getInfoPictures().split(AppConst.divide);
        List<String> test = new LinkedList<>();
        test.add("96df87995e4b482a86c65b9ace4c07ec.jpg");
        test.add("a0691e9888ad4bdcba27440b7fc11548.jpg");
        test.add("a134a52d483846a097f71310c819b6ad.jpg");
        test.add("da1ab9490a6e4d2eb141346607588f37.jpg");
        bannerImageAdapter = new BannerImageAdapter();
        activityInfoDetailBinding.infoBanner.setLifecycleRegistry(getLifecycle())
                .setAdapter(bannerImageAdapter)
                .create(test);
    }

    @Override
    protected void eventBind() {

    }

    private void getInfoData(Integer infoId) {

    }
}
