package com.program.moist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentHomeRecommendBinding;

/**
 * A fragment representing a list of Items.
 */
public class HomeRecommendFragment extends BaseFragment {

    private FragmentHomeRecommendBinding fragmentHomeRecommendBinding;
    public HomeRecommendFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeRecommendFragment newInstance() {
        return new HomeRecommendFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeRecommendBinding = FragmentHomeRecommendBinding.inflate(inflater);
        return fragmentHomeRecommendBinding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void eventBind() {

    }
}