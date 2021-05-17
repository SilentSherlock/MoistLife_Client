package com.program.moist.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.program.moist.R;
import com.program.moist.adapters.FragPageAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentDiscoverBinding;
import com.program.moist.utils.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends BaseFragment {

    private FragmentDiscoverBinding fragmentDiscoverBinding;
    private List<Fragment> fragments;
    public DiscoverFragment() {
        // Required empty public constructor
    }


    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentDiscoverBinding = FragmentDiscoverBinding.inflate(inflater);
        initView();
        eventBind();
        return fragmentDiscoverBinding.getRoot();
    }

    @Override
    protected void initView() {
        if (App.getUserInfo() != null) {
            ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + App.getUserInfo().getUserAvatar(), fragmentDiscoverBinding.discoverUserAvatar);
        }

        //初始化Tab和viewpager2

        fragmentDiscoverBinding.discoverTab.addTab(fragmentDiscoverBinding.discoverTab.newTab().setText(AppConst.Base.recommend));
        fragmentDiscoverBinding.discoverTab.addTab(fragmentDiscoverBinding.discoverTab.newTab().setText(AppConst.Base.follow));
        fragmentDiscoverBinding.discoverTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentDiscoverBinding.discoverViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragments = new ArrayList<>();
        fragments.add(DiscoverRecommendFragment.newInstance());
        fragments.add(DiscoverFollowFragment.newInstance());
        fragmentDiscoverBinding.discoverViewPager.setAdapter(new FragPageAdapter(getChildFragmentManager(), getLifecycle(), fragments));
        fragmentDiscoverBinding.discoverViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //fragmentHomeBinding.homeViewPager.setCurrentItem(position);
                fragmentDiscoverBinding.discoverTab.selectTab(fragmentDiscoverBinding.discoverTab.getTabAt(position));
            }
        });
    }

    @Override
    protected void eventBind() {

    }
}