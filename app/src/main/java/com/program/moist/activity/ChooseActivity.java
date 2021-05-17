package com.program.moist.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityChooseBinding;
import com.program.moist.entity.Category;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;

import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class ChooseActivity extends BaseActivity {

    private ActivityChooseBinding activityChooseBinding;
    private List<Category> childCate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChooseBinding = ActivityChooseBinding.inflate(LayoutInflater.from(this));
        setContentView(activityChooseBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        Category category = (Category) getIntent().getSerializableExtra(AppConst.Info.category);
        invisibleChoose();
        if (category != null) {
            getChildCate(category.getCateId());
        }

    }

    @Override
    protected void eventBind() {

    }

    private void getChildCate(Integer parentCateId) {
        OkGo.<Result>post(AppConst.Info.getChildCate)
                .params("parentCateId", parentCateId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            childCate = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Info.category)),
                                    new TypeToken<List<Category>>(){}.getType()
                            );
                            int rows = activityChooseBinding.chooseRoot.getChildCount();
                            for (int i = 1;i <= rows;i++) {
                                LinearLayout row = (LinearLayout) activityChooseBinding.chooseRoot.getChildAt(i-1);
                                for (int j = 1;j <= row.getChildCount();j++) {
                                    LinearLayout item = (LinearLayout) row.getChildAt(j-1);
                                    if ((i-1) * rows + j < childCate.size()) {
                                        Log.i(TAG, "onSuccess: 更新组件");
                                        int index = (i-1) * rows + j - 1;
                                        item.setVisibility(View.VISIBLE);
                                        ((TextView) item.getChildAt(1)).setText(childCate.get(index).getCateName());
                                        //为category添加点击事件
                                        item.setOnClickListener(v -> {
                                            Intent intent = new Intent(ChooseActivity.this, EditActivity.class);
                                            intent.putExtra("category", childCate.get(index));
                                            startActivity(intent);
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void invisibleChoose() {
        for (int i = 0;i < activityChooseBinding.chooseRoot.getChildCount();i++) {
            LinearLayout row = (LinearLayout) activityChooseBinding.chooseRoot.getChildAt(i);
            for (int j = 0;j < row.getChildCount();j++) {
                LinearLayout item = (LinearLayout) row.getChildAt(j);
                item.setVisibility(View.INVISIBLE);
            }
        }
    }
}
