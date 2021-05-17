package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.BannerImageAdapter;
import com.program.moist.adapters.InfoLeftAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityInfoDetailBinding;
import com.program.moist.entity.Category;
import com.program.moist.entity.Information;
import com.program.moist.entity.User;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/7
 * Description: describe the class
 */
public class InfoDetailActivity extends BaseActivity {

    private ActivityInfoDetailBinding activityInfoDetailBinding;
    private InfoLeftAdapter infoLeftAdapter;
    private int pageIndex = 0;
    private Information information;
    private User owner;
    private Category category;
    private boolean favored;

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
        information = (Information) getIntent().getSerializableExtra("information");
        List<String> data;
        if (information.getInfoPictures() != null && !"".equals(information.getInfoPictures())) {
            data = Arrays.asList(information.getInfoPictures().split(AppConst.divide));
        } else {
            data = new LinkedList<>();
            data.add("96df87995e4b482a86c65b9ace4c07ec.jpg");
            data.add("a0691e9888ad4bdcba27440b7fc11548.jpg");
            data.add("a134a52d483846a097f71310c819b6ad.jpg");
            data.add("da1ab9490a6e4d2eb141346607588f37.jpg");
        }
        BannerImageAdapter bannerImageAdapter = new BannerImageAdapter();
        activityInfoDetailBinding.infoBanner.setLifecycleRegistry(getLifecycle())
                .setAdapter(bannerImageAdapter)
                .create(data);

        getOwner(information.getUserId());
        getCategory(information.getCateId());
        activityInfoDetailBinding.infoDetailTitle.setText(information.getInfoTitle());
        activityInfoDetailBinding.infoDetailPrice.setText(String.valueOf(information.getPrice()));
        activityInfoDetailBinding.infoDetailArea.setText(information.getArea());

        infoLeftAdapter = new InfoLeftAdapter(R.layout.item_home_info);
        infoLeftAdapter.setAnimationEnable(true);
        infoLeftAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
        activityInfoDetailBinding.infoDetailRecommend.setLayoutManager(new LinearLayoutManager(this));
        activityInfoDetailBinding.infoDetailRecommend.setAdapter(infoLeftAdapter);
        getInfoData(pageIndex++);
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    protected void eventBind() {
        activityInfoDetailBinding.infoDetailToolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        activityInfoDetailBinding.infoDetailToolBar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) item -> {
            switch (item.getItemId()) {
                case R.id.info_detail_share:
                    ToastUtil.showToastShort("可以分享啦");
                    break;
                case R.id.info_detail_favor:
                    if (App.isLogin()) {
                        boolean add;
                        if (item.getTitle().equals(getResources().getString(R.string.tool_bar_fav))) {
                            item.setIcon(R.drawable.ic_favor_fill);
                            item.setTitle(getResources().getString(R.string.tool_bar_faved));
                            activityInfoDetailBinding.infoDetailBottomFavor.setImageResource(R.drawable.ic_favor_fill);
                            favored = true;
                            add = true;
                        } else {
                            item.setIcon(R.drawable.ic_favor);
                            item.setTitle(getResources().getString(R.string.tool_bar_fav));
                            activityInfoDetailBinding.infoDetailBottomFavor.setImageResource(R.drawable.ic_favor);
                            favored = false;
                            add = false;
                        }
                        favorAction(information.getInfoId(), add);
                    } else {
                        ToastUtil.showToastShort("请先登录哦");
                    }
                    break;
                case R.id.info_detail_message:
                    chat();
                    break;
                default:
                    return false;
            }
            return true;
        });

        activityInfoDetailBinding.infoDetailRefresh.setEnableLoadMore(true);
        activityInfoDetailBinding.infoDetailRefresh.setOnLoadMoreListener(refreshLayout -> getInfoData(pageIndex++));

        activityInfoDetailBinding.infoDetailOwner.itemUserName.setOnClickListener(v -> {
            ToastUtil.showToastShort("打开Owner主页，可是还在开发中呢");
        });
        activityInfoDetailBinding.infoDetailOwner.itemButton.setOnClickListener(v -> {
            if (App.isLogin()) {
                Button button = (Button) v;
                boolean add;
                if (button.getText().equals(getResources().getString(R.string.button_follow))) {
                    button.setText(getResources().getString(R.string.button_followed));
                    button.setBackground(getDrawable(R.drawable.button_2_checked));
                    add = true;
                } else {
                    button.setText(getResources().getString(R.string.button_follow));
                    button.setBackground(getDrawable(R.drawable.button_2));
                    add = false;
                }
                followAction(owner.getUserId(), add);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        activityInfoDetailBinding.infoDetailBottomFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        activityInfoDetailBinding.infoDetailBottomButton.setOnClickListener(v -> chat());
    }

    private void chat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user", owner);
        intent.putExtra("host", "127.0.0.1");
        startActivity(intent);
    }
    /**
     * 下拉推荐更多同类信息
     */
    private void getInfoData(int pageIndex) {
        if (App.isLogin()) {
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
                                activityInfoDetailBinding.infoDetailRefresh.finishLoadMore();
                            } else {
                                activityInfoDetailBinding.infoDetailRefresh.finishLoadMoreWithNoMoreData();
                            }

                        }
                    });
        }
    }

    /**
     * 收藏相关操作
     * @param infoId
     * @param add
     */
    private void favorAction(Integer infoId, Boolean add) {
        OkGo.<Result>post(add ? AppConst.Info.addUserFavInfo : AppConst.Info.deleteUserFavInfo)
                .params("userId", App.getUserInfo().getUserId())
                .params("infoId", infoId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            if (add) ToastUtil.showToastShort("收藏成功~");
                            else ToastUtil.showToastShort("取消收藏啦~");
                        } else {
                            Log.i(TAG, "onSuccess: " + result.getStatus());
                        }
                    }
                });
    }

    /**
     * 获取owner信息
     */
    private void getOwner(Integer userId) {
        OkGo.<Result>post(AppConst.User.getUserById)
                .params("userId", userId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            owner = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.user)),
                                    new TypeToken<User>(){}.getType()
                            );
                            activityInfoDetailBinding.infoDetailOwner.itemUserName.setText(owner.getUserName());
                            activityInfoDetailBinding.infoDetailOwner.itemUserLocation.setText(owner.getLocation());
                            ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + owner.getUserAvatar(),
                                    activityInfoDetailBinding.infoDetailOwner.itemUserAvatar);
                        } else {
                            Log.i(TAG, "onSuccess: " + result.getStatus() + " " + result.getDescription());
                        }
                    }
                });
    }

    /**
     * 获取info的cate信息
     * @param cateId
     */
    private void getCategory(Integer cateId) {
        OkGo.<Result>post(AppConst.Info.getCateById)
                .params("cateId", cateId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            category = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Info.category)),
                                    new TypeToken<Category>(){}.getType()
                            );
                            activityInfoDetailBinding.infoDetailCate.setText(category.getCateName());
                        } else {
                            Log.i(TAG, "onSuccess: " + result.getStatus());
                        }
                    }
                });
    }

    /**
     * 关注相关操作
     * @param toId
     * @param add
     */
    private void followAction(Integer toId, Boolean add) {
        OkGo.<Result>post(add ? AppConst.User.addFollow : AppConst.User.deleteFollow)
                .params("fromUserId", App.getUserInfo().getUserId())
                .params("toUserId", toId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            if (add) ToastUtil.showToastShort("关注TA哦*-*");
                            else ToastUtil.showToastShort("不再关注TA了:-)");
                        }
                    }
                });
    }

    private void getHost() {
        /*OkGo.<Result>post()
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {

                    }
                });*/
    }
}
