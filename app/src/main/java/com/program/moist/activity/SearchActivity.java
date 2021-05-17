package com.program.moist.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.InfoLeftAdapter;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivitySearchBinding;
import com.program.moist.entity.Information;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: SilentSherlock
 * Date: 2021/5/14
 * Description: describe the class
 */
public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding activitySearchBinding;
    private ArrayAdapter<String> arrayAdapter;
    private InfoLeftAdapter infoLeftAdapter;
    private int pageIndex = 1;
    private String cateName = "all";
    private String keyWord;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = ActivitySearchBinding.inflate(LayoutInflater.from(this));
        setContentView(activitySearchBinding.getRoot());
        initView();
        eventBind();
        setContentView(activitySearchBinding.getRoot());
    }

    @Override
    protected void initView() {
        //初始化类别选择
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<String> categories = new ArrayList<>();
        categories.add("全部");
        categories.add("二手");
        categories.add("家电");
        categories.add("母婴");
        categories.add("文体");
        categories.add("户外");
        arrayAdapter.addAll(categories);
        activitySearchBinding.searchScaleChoose.setAdapter(arrayAdapter);
        activitySearchBinding.searchScaleChoose.setSelection(0);

        //设置信息流适配器
        infoLeftAdapter = new InfoLeftAdapter(R.layout.item_home_info);
        infoLeftAdapter.setAnimationEnable(true);
        infoLeftAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        activitySearchBinding.searchContent.setAdapter(infoLeftAdapter);
        activitySearchBinding.searchContent.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void eventBind() {
        activitySearchBinding.searchTextLayout.setStartIconOnClickListener(v -> {
            //搜索逻辑
            String word = Objects.requireNonNull(activitySearchBinding.searchText.getText()).toString().trim();
            if (!word.equals("")) {
                activitySearchBinding.searchContent.removeAllViews();
                infoLeftAdapter.notifyDataSetChanged();
                ToastUtil.showToastShort("正在搜索");
                keyWord = word;
                getInfoPage(keyWord, cateName, pageIndex++, true);
            } else {
                ToastUtil.showToastShort("搜索内容为空哦~");
            }
        });

        activitySearchBinding.searchScaleChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        cateName = "all";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activitySearchBinding.searchRefresh.setEnableLoadMore(true);
        activitySearchBinding.searchRefresh.setOnLoadMoreListener(refreshLayout -> getInfoPage(keyWord, cateName, pageIndex++, false));
    }

    /**
     *
     * @param keyWord
     * @param cateName
     * @param pageIndex
     * @param res true-清空页面后刷新 false--添加新数据
     */
    private void getInfoPage(String keyWord, String cateName, Integer pageIndex, boolean res) {
        OkGo.<Result>post(AppConst.Search.searchInfoPage)
                .params("keyWord", keyWord)
                .params("cateName", cateName)
                .params("pageIndex", pageIndex)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<Information> information = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Base.infos)),
                                    new TypeToken<List<Information>>(){}.getType()
                            );
                            if (res) {
                                infoLeftAdapter.setNewInstance(information);
                            } else {
                                infoLeftAdapter.addData(information);
                            }
                            activitySearchBinding.searchRefresh.finishLoadMore();
                        } else {
                            ToastUtil.showToastLong(result.getDescription());
                            activitySearchBinding.searchRefresh.finishLoadMoreWithNoMoreData();
                        }
                    }
                });
    }
}
