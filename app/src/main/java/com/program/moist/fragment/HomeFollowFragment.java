package com.program.moist.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.activity.InfoDetailActivity;
import com.program.moist.adapters.InfoRecycleAdapter;
import com.program.moist.adapters.UserRecycleAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentHomeFollowBinding;
import com.program.moist.entity.Information;
import com.program.moist.entity.User;
import com.program.moist.entity.item.InfoUser;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/4/30
 * Description: describe the class
 */
public class HomeFollowFragment extends BaseFragment {


    private FragmentHomeFollowBinding fragmentHomeFollowBinding;
    private InfoRecycleAdapter infoRecycleAdapter;
    private UserRecycleAdapter userRecycleAdapter;
    private List<User> followings;
    private HashMap<Integer, List<Information>> userInfoMap = new HashMap<>();
    private List<InfoUser> itemInfoUser;
    private Handler handler;
    public static final int FINISH = 1;

    CountDownLatch countDownLatch;//??????????????????
    private int infoPageIndex = 0;
    private int userPageIndex = 0;

    public HomeFollowFragment() {

    }

    public static HomeFollowFragment newInstance() {
        return new HomeFollowFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeFollowBinding = FragmentHomeFollowBinding.inflate(inflater);

        initView();
        eventBind();
        return fragmentHomeFollowBinding.getRoot();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView() {
        User user = App.getUserInfo();
        infoRecycleAdapter = new InfoRecycleAdapter(R.layout.item_home_info);
        infoRecycleAdapter.getLoadMoreModule().setEnableLoadMoreEndClick(true);
        infoRecycleAdapter.getLoadMoreModule().checkDisableLoadMoreIfNotFullPage();

        infoRecycleAdapter.setAnimationEnable(true);
        infoRecycleAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);

        userRecycleAdapter = new UserRecycleAdapter(R.layout.item_user_card);
        userRecycleAdapter.getLoadMoreModule().setEnableLoadMoreEndClick(true);
        userRecycleAdapter.getLoadMoreModule().checkDisableLoadMoreIfNotFullPage();

        if (user == null) {
            Log.i(TAG, "initView: ??????????????????");
            fragmentHomeFollowBinding.followContent.setAdapter(userRecycleAdapter);
            getUserPage(userPageIndex++);
        } else {
            Log.i(TAG, "initView: ??????????????????");
            getUserFollowing();//????????????
        }
        fragmentHomeFollowBinding.followContent.setLayoutManager(new LinearLayoutManager(getContext()));

        //????????????????????????UI
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FINISH:
                        generateInfoUser(null);
                        if (itemInfoUser != null && itemInfoUser.size() != 0) {
                            infoRecycleAdapter.addData(itemInfoUser);
                            itemInfoUser = null;
                            infoRecycleAdapter.getLoadMoreModule().loadMoreComplete();
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void eventBind() {
        fragmentHomeFollowBinding.followRefresh.setEnableLoadMore(true);
        fragmentHomeFollowBinding.followRefresh.setOnLoadMoreListener(refreshLayout -> {
            generateInfoUser(null);
            if (itemInfoUser != null && itemInfoUser.size() != 0) {
                infoRecycleAdapter.addData(itemInfoUser);
                itemInfoUser = null;
                fragmentHomeFollowBinding.followRefresh.finishLoadMore();
            } else {
                fragmentHomeFollowBinding.followRefresh.finishLoadMoreWithNoMoreData();
            }
        });
        infoRecycleAdapter.setOnItemClickListener((adapter, view, position) -> {
            TextView hide = (TextView)((LinearLayout) view).getChildAt(0);
            int infoId = Integer.parseInt((String) hide.getText());
            OkGo.<Result>post(AppConst.Info.getInfoById)
                    .params("infoId", infoId)
                    .execute(new ResultCallback() {
                        @Override
                        public void onSuccess(Response<Result> response) {
                            Result result = response.body();
                            if (result.getStatus() == Status.SUCCESS) {
                                Information information = GsonUtil.fromJson(
                                        GsonUtil.toJson(result.getResultMap().get("INFO")),
                                        new TypeToken<Information>(){}.getType()
                                );
                                Log.i(TAG, "onSuccess: info" + information.toString());
                                Intent intent = new Intent(getActivity(), InfoDetailActivity.class);
                                intent.putExtra("information", information);
                                startActivity(intent);
                            } else {
                                Log.i(TAG, "onSuccess: " + result.getStatus());
                            }
                        }
                    });
        });
        //fragmentHomeFollowBinding.followRefresh;
        //?????????????????????????????????????????????????????????????????????
        userRecycleAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getUserPage(userPageIndex++);
            }
        });
    }


    private void getInfoData(int userId) {
        OkGo.<Result>get(AppConst.Info.getInfoByUserId)
                .params("userId", userId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            Log.i(TAG, "onSuccess: get page info");
                            List<Information> informationList = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Base.infos)),
                                    new TypeToken<List<Information>>(){}.getType());
                            if (userInfoMap == null) userInfoMap = new HashMap<>();
                            userInfoMap.put(userId, informationList);
                        }
                        Log.i(TAG, "onSuccess: ???????????????");
                        countDownLatch.countDown();//???????????????
                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void getUserFollowing() {
        Log.i(TAG, "getUserFollowing: 1 " + Thread.currentThread().getName());
        OkGo.<Result>post(AppConst.User.getFollowing)
                .params("fromId", App.getUserInfo().getUserId())
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {//?????????????????????????????????????????????info
                            followings = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.users)),
                                    new TypeToken<List<User>>(){}.getType()
                            );
                            fragmentHomeFollowBinding.followContent.setAdapter(infoRecycleAdapter);
                            countDownLatch = new CountDownLatch(followings.size());
                            Log.i(TAG, "onSuccess: ??????????????????" + countDownLatch.getCount());
                            for (int i = 0; i < followings.size(); i++) {
                                Log.i(TAG, "onSuccess: ?????????" + i + "?????????");
                                getInfoData(followings.get(i).getUserId());
                                //getInfoData??????????????????????????????????????????????????????????????????????????????
                            }
                            new Thread(()->{
                                try {
                                    Log.i(TAG, "onSuccess: getUserFollowing ??????????????????" + Thread.currentThread().getName());
                                    countDownLatch.await();
                                    Message message = new Message();
                                    message.what = FINISH;
                                    handler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        } else if (result.getStatus() == Status.DEFAULT) {//????????????????????????????????????
                            fragmentHomeFollowBinding.followContent.setAdapter(userRecycleAdapter);
                            getUserPage(userPageIndex++);
                        }
                    }
                });
    }

    /**
     * ??????????????????
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
                            userRecycleAdapter.getLoadMoreModule().loadMoreComplete();
                        } else {
                            userRecycleAdapter.getLoadMoreModule().loadMoreEnd();
                        }
                    }
                });
    }

    /**
     * ??????????????????10?????????
     * @param itemCount
     */
    private void generateInfoUser(Integer itemCount) {
        if (itemCount == null) itemCount = 10;
        itemInfoUser = new LinkedList<>();
        Log.i(TAG, "generateInfoUser: following??????" + followings.size());
        for (int i = 0; i < Math.min(itemCount, followings.size()); i++) {
            InfoUser infoUser = new InfoUser();
            User user = followings.get(i);
            List<Information> informationList = userInfoMap.get(user.getUserId());
            if (informationList != null && informationList.size() != 0) {
                infoUser.fill(user, informationList.get(0));
                informationList.remove(0);
                itemInfoUser.add(infoUser);
            }
            Log.i(TAG, "generateInfoUser: itemInfoUser??????" + itemInfoUser.size());
        }
    }

}
