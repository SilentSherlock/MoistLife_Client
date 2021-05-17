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
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentDiscoverBinding;
import com.program.moist.databinding.FragmentDiscoverRecommendBinding;
import com.program.moist.entity.Post;
import com.program.moist.entity.User;
import com.program.moist.entity.item.Message;
import com.program.moist.entity.item.MessageUser;
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
public class DiscoverRecommendFragment extends BaseFragment {

    private FragmentDiscoverRecommendBinding fragmentDiscoverRecommendBinding;
    private PostCardAdapter postCardAdapter;
    public DiscoverRecommendFragment() {

    }
    public static DiscoverRecommendFragment newInstance() {
        return new DiscoverRecommendFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentDiscoverRecommendBinding = FragmentDiscoverRecommendBinding.inflate(inflater, container, false);
        initView();
        eventBind();
        return fragmentDiscoverRecommendBinding.getRoot();
    }

    @Override
    protected void initView() {
        postCardAdapter = new PostCardAdapter(R.layout.item_post_card);
        fragmentDiscoverRecommendBinding.discoverRecommendContent.setAdapter(postCardAdapter);
        fragmentDiscoverRecommendBinding.discoverRecommendContent.setLayoutManager(new LinearLayoutManager(getContext()));

        getDefaultPost();
    }

    @Override
    protected void eventBind() {

    }

    private void getDefaultPost() {
        OkGo.<Result>post(AppConst.Post.defaultPost)
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
