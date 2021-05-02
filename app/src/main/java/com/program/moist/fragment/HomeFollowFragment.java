package com.program.moist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentHomeFollowBinding;

/**
 * Author: SilentSherlock
 * Date: 2021/4/30
 * Description: describe the class
 */
public class HomeFollowFragment extends BaseFragment {


    private FragmentHomeFollowBinding fragmentHomeFollowBinding;
    public HomeFollowFragment() {

    }

    public static HomeFollowFragment newInstance() {
        return new HomeFollowFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeFollowBinding = FragmentHomeFollowBinding.inflate(inflater);
        return fragmentHomeFollowBinding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void eventBind() {

    }
}
