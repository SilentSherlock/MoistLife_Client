package com.program.moist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.InfoLeftAdapter;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityCategoryBinding;
import com.program.moist.entity.Category;
import com.program.moist.entity.Information;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/14
 * Description: describe the class
 */
public class CategoryActivity extends BaseActivity {

    private ActivityCategoryBinding activityCategoryBinding;
    private Category category;
    private InfoLeftAdapter infoLeftAdapter;
    private int searchPageIndex = 1;
    private int pageIndex = 1;
    private String keyWord;
    private boolean search = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCategoryBinding = ActivityCategoryBinding.inflate(LayoutInflater.from(this));
        setContentView(activityCategoryBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        category = (Category) getIntent().getSerializableExtra("category");
        activityCategoryBinding.searchText.setHint("搜索" + category.getCateName());

        infoLeftAdapter = new InfoLeftAdapter(R.layout.item_home_info);
        activityCategoryBinding.categoryContent.setAdapter(infoLeftAdapter);
        activityCategoryBinding.categoryContent.setLayoutManager(new LinearLayoutManager(this));
        activityCategoryBinding.categoryRefresh.setEnableLoadMore(true);

        infoLeftAdapter.setAnimationEnable(true);
        infoLeftAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        getInfoPage(category.getCateId(), pageIndex++);
        getStatusCount(category.getCateId());
    }

    @Override
    protected void eventBind() {
        activityCategoryBinding.searchTextLayout.setStartIconOnClickListener(v -> {
            finish();
        });
        activityCategoryBinding.searchTextLayout.setEndIconOnClickListener(v -> {
            String text = Objects.requireNonNull(activityCategoryBinding.searchText.getText()).toString().trim();
            if (!text.equals("")) {
                search = true;
                keyWord = text;
                ToastUtil.showToastShort("开始搜索");
                getInfoPageBySearch(keyWord, category.getCateName(), searchPageIndex++, true);
            } else {
                ToastUtil.showToastShort("搜索内容不能为空哦~");
            }
        });
        activityCategoryBinding.categoryRefresh.setOnLoadMoreListener(refreshLayout -> {
            if (search) {
                getInfoPageBySearch(keyWord, category.getCateName(), searchPageIndex++, false);
            } else {
                getInfoPage(category.getCateId(), pageIndex++);
            }
        });
    }

    //获取该类别的统计数据
    private void getStatusCount(Integer cateId) {
        OkGo.<Result>post(AppConst.Info.getStatusCountByCateId)
                .params("cateId", cateId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            Float ongoing = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Info.ongoing)),
                                    new TypeToken<Float>(){}.getType()
                            );
                            Float finish = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Info.finish)),
                                    new TypeToken<Float>(){}.getType()
                            );

                            float ongoingPercent = ongoing / (ongoing + finish) * 100;
                            float finishPercent = finish / (ongoing + finish) * 100;
                            List<PieEntry> data = new ArrayList<>();
                            data.add(new PieEntry(ongoingPercent, "正在进行"));
                            data.add(new PieEntry(finishPercent, "已经完成"));

                            PieDataSet pieDataSet = new PieDataSet(data, category.getCateName());

                            ArrayList<Integer> colors = new ArrayList<>();
                            colors.add(getResources().getColor(R.color.message_bubble));
                            colors.add(getResources().getColor(R.color.orange));

                            pieDataSet.setColors(colors);

                            PieData pieData = new PieData(pieDataSet);
                            pieData.setDrawValues(true);
                            pieData.setValueFormatter(new PercentFormatter());
                            pieData.setValueTextSize(12f);

                            Description description = new Description();
                            description.setText("百分比");
                            activityCategoryBinding.categoryChart.setData(pieData);
                            activityCategoryBinding.categoryChart.invalidate();
                            activityCategoryBinding.categoryChart.setDescription(description);
                        }
                    }
                });
    }

    //直接浏览拉取信息流
    private void getInfoPage(Integer cateId, Integer pageIndex) {
        OkGo.<Result>post(AppConst.Info.getInfoByPage)
                .params("index", pageIndex)
                .params("name", "cate_id")
                .params("value", cateId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<Information> information = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Base.infos)),
                                    new TypeToken<List<Information>>(){}.getType()
                            );
                            infoLeftAdapter.addData(information);
                            activityCategoryBinding.categoryRefresh.finishLoadMore();
                        } else {
                            ToastUtil.showToastLong(result.getDescription());
                            activityCategoryBinding.categoryRefresh.finishLoadMoreWithNoMoreData();
                        }
                    }
                });
    }

    /**
     *
     * @param keyWord
     * @param cateName
     * @param searchPageIndex
     * @param res true 清空刷新 false 下拉刷新
     */
    private void getInfoPageBySearch(String keyWord, String cateName, Integer searchPageIndex, boolean res) {
        OkGo.<Result>post(AppConst.Search.searchInfoPage)
                .params("keyWord", keyWord)
                .params("cateName", cateName)
                .params("pageIndex", searchPageIndex)
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
                            activityCategoryBinding.categoryRefresh.finishLoadMore();
                        } else {
                            ToastUtil.showToastLong(result.getDescription());
                            activityCategoryBinding.categoryRefresh.finishLoadMoreWithNoMoreData();
                        }
                    }
                });
    }
}
