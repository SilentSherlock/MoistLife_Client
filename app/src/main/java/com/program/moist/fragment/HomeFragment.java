package com.program.moist.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.FragPageAdapter;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentHomeBinding;
import com.program.moist.entity.Category;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.program.moist.base.AppConst.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding fragmentHomeBinding;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater);
        initView();
        eventBind();
        return fragmentHomeBinding.getRoot();
    }

    @Override
    protected void initView() {

        //初始化category
        getCate();
        //初始化Tab和viewpager2

        fragmentHomeBinding.homeTab.addTab(fragmentHomeBinding.homeTab.newTab().setText(AppConst.Base.recommend));
        fragmentHomeBinding.homeTab.addTab(fragmentHomeBinding.homeTab.newTab().setText(AppConst.Base.follow));
        fragmentHomeBinding.homeTab.addOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        ToastUtil.showToastShort("0");
                        break;
                    case 1:
                        ToastUtil.showToastShort("1");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeRecommendFragment.newInstance());
        fragments.add(HomeFollowFragment.newInstance());
        fragmentHomeBinding.homeViewPager.setAdapter(new FragPageAdapter(getChildFragmentManager(), getLifecycle(), fragments));
        fragmentHomeBinding.homeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                fragmentHomeBinding.homeTab.selectTab(fragmentHomeBinding.homeTab.getTabAt(position));
            }
        });
    }
    @Override
    protected void eventBind() {

    }

    /**
     * 获得一级分类
     */
    private void getCate() {
        OkGo.<Result>get(AppConst.Info.getAllCate)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            Map<String, Object> resultMap = result.getResultMap();
                            //将LinkHashMap先转为string再转为所需对象
                            List<Category> categories = GsonUtil.fromJson(GsonUtil.toJson(resultMap.get(AppConst.Base.category)),
                                    new TypeToken<List<Category>>(){}.getType());
                            if (categories != null) {
                                for (int i = 1;i <= fragmentHomeBinding.category.getChildCount();i++) {
                                    LinearLayout row = (LinearLayout) fragmentHomeBinding.category.getChildAt(i-1);
                                    for (int j = 1; j <= row.getChildCount(); j++) {
                                        int index = i * j - 1;
                                        LinearLayout item = (LinearLayout) row.getChildAt(j-1);
                                        if (index == AppConst.Base.category_num - 1) {
                                            ((TextView) item.getChildAt(1)).setText("更多");
                                        } else {
                                        /*((TextView) linearLayout.getChildAt(1)).setText(GsonUtil.fromJson(
                                                GsonUtil.toJson(categories.get(i)),
                                                Category.class
                                        ).getCateName());*/
                                            ((TextView)(item.getChildAt(1))).setText(categories.get(index).getCateName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }
}