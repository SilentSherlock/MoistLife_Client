package com.program.moist.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.activity.CategoryActivity;
import com.program.moist.activity.ChooseActivity;
import com.program.moist.activity.EditActivity;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentAddBinding;
import com.program.moist.entity.Category;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends BaseFragment {

    private FragmentAddBinding fragmentAddBinding;
    private List<Category> topCate;//一级分类
    public AddFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentAddBinding = FragmentAddBinding.inflate(inflater, container, false);
        initView();
        eventBind();

        return fragmentAddBinding.getRoot();
    }

    @Override
    protected void initView() {
        getCate();
    }

    @Override
    protected void eventBind() {
        ClickListener clickListener = new ClickListener();
        fragmentAddBinding.addLocalLife.setOnClickListener(clickListener);
        fragmentAddBinding.addFamilyService.setOnClickListener(clickListener);
        fragmentAddBinding.addSecondEntity.setOnClickListener(clickListener);
        fragmentAddBinding.addFriend.setOnClickListener(clickListener);
    }

    class ClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ChooseActivity.class);
            switch (v.getId()) {
                case R.id.add_local_life:
                    intent.putExtra(AppConst.Info.category, topCate.get(0));
                    startActivity(intent);
                    break;
                case R.id.add_second_entity:
                    intent.putExtra(AppConst.Info.category, topCate.get(1));
                    startActivity(intent);
                    break;
                case R.id.add_family_service:
                    intent.putExtra(AppConst.Info.category, topCate.get(2));
                    startActivity(intent);
                    break;
                case R.id.add_friend:
                    startActivity(new Intent(getContext(), EditActivity.class));
                    break;
                default:
                    ToastUtil.showToastShort("shit");
            }
        }
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
                            if (categories != null && categories.size() != 0) {
                                topCate = categories;
                            }
                        }
                    }
                });
    }
}