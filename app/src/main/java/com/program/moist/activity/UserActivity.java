package com.program.moist.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.InfoLeftAdapter;
import com.program.moist.adapters.PostCardAdapter;
import com.program.moist.adapters.UserRecycleAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityUserBinding;
import com.program.moist.entity.Information;
import com.program.moist.entity.Post;
import com.program.moist.entity.User;
import com.program.moist.entity.item.PostUser;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: SilentSherlock
 * Date: 2021/5/16
 * Description: describe the class
 */
public class UserActivity extends BaseActivity {

    private ActivityUserBinding activityUserBinding;
    private PostCardAdapter postCardAdapter;
    private InfoLeftAdapter infoLeftAdapter;
    private UserRecycleAdapter userRecycleAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserBinding = ActivityUserBinding.inflate(LayoutInflater.from(this));
        setContentView(activityUserBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        Integer kindType = getIntent().getIntExtra(AppConst.Base.kind, 0);
        switch (kindType) {
            case 0:
                activityUserBinding.userToolBar.setTitle("我的发布");
            case 1:
                activityUserBinding.userToolBar.setTitle("我的收藏");
                getUserInfo();
                break;
            case 2:
                activityUserBinding.userToolBar.setTitle("我的帖子");
                getUserPost();
                break;
            case 3:
                activityUserBinding.userToolBar.setTitle("我的关注");
                getUserFollowing();
                break;
        }
        activityUserBinding.userContent.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void eventBind() {
        activityUserBinding.userToolBar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void getUserInfo() {
        OkGo.<Result>post(AppConst.Info.getInfoByUserId)
                .params("userId", App.getUserInfo().getUserId())
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<Information> informationList = GsonUtil.fromJson(GsonUtil.toJson(result.getResultMap().get(AppConst.Base.infos)),
                                    new TypeToken<List<Information>>(){}.getType());
                            infoLeftAdapter = new InfoLeftAdapter(R.layout.item_home_info);
                            activityUserBinding.userContent.setAdapter(infoLeftAdapter);
                            infoLeftAdapter.addData(informationList);
                        }
                    }
                });
    }

    private void getUserPost() {
        OkGo.<Result>post(AppConst.Post.getPostByUserId)
                .params("userId", App.getUserInfo().getUserId())
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<Post> posts = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Post.DEFAULT_POST)),
                                    new TypeToken<List<Post>>(){}.getType()
                            );
                            postCardAdapter = new PostCardAdapter(R.layout.item_post_card);
                            activityUserBinding.userContent.setAdapter(postCardAdapter);
                            List<PostUser> postUsers = new ArrayList<>();
                            for (Post post :
                                    posts) {
                                PostUser postUser = PostUser.createByAll(post, App.getUserInfo());
                                postUsers.add(postUser);
                            }
                            postCardAdapter.addData(postUsers);
                        }
                    }
                });
    }

    private void getUserFollowing() {
        OkGo.<Result>post(AppConst.User.getFollowing)
                .params("userId", App.getUserInfo().getUserId())
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<User> users = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.users)),
                                    new TypeToken<List<User>>(){}.getType()
                            );
                            userRecycleAdapter = new UserRecycleAdapter(R.layout.item_user_card);
                            activityUserBinding.userContent.setAdapter(userRecycleAdapter);

                            userRecycleAdapter.addData(users);
                        } else {
                            ToastUtil.showToastShort(result.getDescription());
                        }
                    }
                });
    }
}
