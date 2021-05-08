package com.program.moist.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.InfoLeftAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentHomeRecommendBinding;
import com.program.moist.entity.Information;
import com.program.moist.entity.User;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;

import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * A fragment representing a list of Items.
 */
public class HomeRecommendFragment extends BaseFragment {

    private FragmentHomeRecommendBinding fragmentHomeRecommendBinding;
    private int pageIndex = 0;
    private InfoLeftAdapter infoLeftAdapter;
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

        initView();
        eventBind();
        return fragmentHomeRecommendBinding.getRoot();
    }

    @Override
    protected void initView() {
        infoLeftAdapter = new InfoLeftAdapter(R.layout.item_home_info);
        fragmentHomeRecommendBinding.recommendRefresh.setEnableLoadMore(true);
        fragmentHomeRecommendBinding.recommendRefresh.setOnLoadMoreListener(refreshLayout -> getInfoData(++pageIndex));
        infoLeftAdapter.getLoadMoreModule().checkDisableLoadMoreIfNotFullPage();
        fragmentHomeRecommendBinding.recommendContent.setAdapter(infoLeftAdapter);
        fragmentHomeRecommendBinding.recommendContent.setLayoutManager(new LinearLayoutManager(getContext()));
        getInfoData(pageIndex);
    }

    @Override
    protected void eventBind() {
    }

    protected void getInfoData(int pageIndex) {
        User user = App.getUserInfo();
        if (user == null) {
            OkGo.<Result>get(AppConst.Info.getDefaultInfo)
                    .execute(new ResultCallback() {
                        @Override
                        public void onSuccess(Response<Result> response) {
                            Result result = response.body();
                            if (result.getStatus() == Status.SUCCESS) {
                                Log.i(TAG, "onSuccess: get default info");
                                List<Information> informationList = GsonUtil.fromJson(GsonUtil.toJson(result.getResultMap().get(AppConst.Base.default_info)),
                                        new TypeToken<List<Information>>(){}.getType());
                                infoLeftAdapter.addData(informationList);
                            }
                        }
                    });
        } else {
            //登录之后可以根据搜索历史和浏览记录个性化推荐
            //目前先简单分页获取
            OkGo.<Result>get(AppConst.Info.getInfoByPage)
                    .params("index", pageIndex)
                    .params("name", "area")
                    .params("value", "London")
                    .execute(new ResultCallback() {
                        @Override
                        public void onSuccess(Response<Result> response) {
                            Result result = response.body();
                            if (result.getStatus() == Status.SUCCESS) {
                                Log.i(TAG, "onSuccess: get page info");
                                List<Information> informationList = GsonUtil.fromJson(GsonUtil.toJson(result.getResultMap().get(AppConst.Base.infos)),
                                        new TypeToken<List<Information>>(){}.getType());
                                infoLeftAdapter.addData(informationList);
                                fragmentHomeRecommendBinding.recommendRefresh.finishLoadMore();
                            } else {
                                fragmentHomeRecommendBinding.recommendRefresh.finishLoadMoreWithNoMoreData();
                            }

                        }
                    });
        }

    }
}