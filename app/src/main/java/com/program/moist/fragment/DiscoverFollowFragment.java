package com.program.moist.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.PostCardAdapter;
import com.program.moist.adapters.UserRecycleAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentDiscoverFollowBinding;
import com.program.moist.databinding.FragmentDiscoverRecommendBinding;
import com.program.moist.entity.Post;
import com.program.moist.entity.User;
import com.program.moist.entity.item.PostUser;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;

import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/14
 * Description: describe the class
 */
public class DiscoverFollowFragment extends BaseFragment {

    private FragmentDiscoverFollowBinding fragmentDiscoverFollowBinding;
    private PostCardAdapter postCardAdapter;
    private UserRecycleAdapter userRecycleAdapter;
    private int userPageIndex = 1;
    public static DiscoverFollowFragment newInstance() {
        return new DiscoverFollowFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentDiscoverFollowBinding = FragmentDiscoverFollowBinding.inflate(inflater, container, false);
        initView();
        eventBind();

        return fragmentDiscoverFollowBinding.getRoot();
    }

    @Override
    protected void initView() {
        postCardAdapter = new PostCardAdapter(R.layout.item_post_card);
        userRecycleAdapter = new UserRecycleAdapter(R.layout.item_user_card);
        fragmentDiscoverFollowBinding.discoverFollowContent.setLayoutManager(new LinearLayoutManager(getContext()));
        if (App.isLogin()) {
            fragmentDiscoverFollowBinding.discoverFollowContent.setAdapter(postCardAdapter);
            getFollowPost();
        } else {
            fragmentDiscoverFollowBinding.discoverFollowContent.setAdapter(userRecycleAdapter);
            getUserPage(userPageIndex++);
        }
    }

    @Override
    protected void eventBind() {

    }

    /**
     * 分页获取用户
     * @param userPageIndex
     */
    private void getUserPage(int userPageIndex) {
        OkGo.<Result>post(AppConst.User.getUserByPage)
                .params("index", userPageIndex)
                .params("name", "user_kind")
                .params("value", 2)//U_Medium
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<User> users = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.users)),
                                    new TypeToken<List<User>>(){}.getType());
                            userRecycleAdapter.addData(users);
                            //userRecycleAdapter.getLoadMoreModule().loadMoreComplete();
                        } else {
                            //userRecycleAdapter.getLoadMoreModule().loadMoreEnd();
                        }
                    }
                });
    }

    private void getFollowPost() {
        OkGo.<Result>post(AppConst.Post.getFollowPost)
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
                            for (Post post :
                                    posts) {
                                getUser(post.getUserId(), post);
                            }
                        } else if (result.getStatus() == Status.DEFAULT) {
                            List<User> users = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.users)),
                                    new TypeToken<List<User>>(){}.getType());
                            userRecycleAdapter.addData(users);
                            fragmentDiscoverFollowBinding.discoverFollowContent.setAdapter(userRecycleAdapter);
                        }
                    }
                });
    }

    private void getUser(Integer userId, Post post) {
        OkGo.<Result>post(AppConst.User.getUserById)
                .params("userId", userId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            User user = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.user)),
                                    new TypeToken<User>(){}.getType()
                            );
                            postCardAdapter.addData(PostUser.createByAll(post, user));
                        } else {
                            Log.i(TAG, "onSuccess: 未获得用户");
                        }
                    }
                });
    }
}
