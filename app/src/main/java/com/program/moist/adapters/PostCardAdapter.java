package com.program.moist.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.activity.LoginActivity;
import com.program.moist.activity.PostDetailActivity;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.entity.item.PostUser;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Author: SilentSherlock
 * Date: 2021/5/14
 * Description: describe the class
 */
public class PostCardAdapter extends BaseQuickAdapter<PostUser, BaseViewHolder> {

    public PostCardAdapter(int layoutResId) {
        super(layoutResId);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PostUser postUser) {
        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + postUser.getUser().getUserAvatar(), baseViewHolder.getView(R.id.item_post_user_avatar));
        baseViewHolder.setText(R.id.item_post_user_name, postUser.getUser().getUserName());
        baseViewHolder.setText(R.id.item_post_title, postUser.getPost().getPostTitle());
        baseViewHolder.setText(R.id.item_post_detail, postUser.getPost().getPostTitle());
        baseViewHolder.setText(R.id.item_post_comment_account, AppConst.getRandomNumber(null));
        baseViewHolder.setText(R.id.item_post_thumb_up_account, AppConst.getRandomNumber(null));

        String picture = postUser.getPost().getPostPictures();
        if (picture == null || picture.equals("")) {
            //baseViewHolder.getView(R.id.item_post_image).setVisibility(View.GONE);
        } else {
            String[] pictures = picture.split(AppConst.divide_2);
            ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + pictures[0], baseViewHolder.getView(R.id.item_post_image));
        }

        //为卡片设置点击事件
        baseViewHolder.itemView.setOnClickListener(v -> {
            //ToastUtil.showToastShort("跳转到post详情");
            Intent intent = new Intent(getContext(), PostDetailActivity.class);
            intent.putExtra("postUser", postUser);
            getContext().startActivity(intent);
        });
        baseViewHolder.getView(R.id.item_post_user_name).setOnClickListener(v -> {
            ToastUtil.showToastShort("跳转到用户详情");
        });
        baseViewHolder.getView(R.id.item_post_user_avatar).setOnClickListener(v -> {
            ToastUtil.showToastShort("跳转到用户详情");
        });
        //为关注设置点击事件
        baseViewHolder.getView(R.id.item_post_button).setOnClickListener(v -> {
            if (App.isLogin()) {
                Button button = (Button) v;
                boolean add;
                if (button.getText().equals(getContext().getResources().getString(R.string.button_follow))) {
                    button.setText(getContext().getResources().getString(R.string.button_followed));
                    button.setBackground(getContext().getDrawable(R.drawable.button_2_checked));
                    add = true;
                } else {
                    button.setText(getContext().getResources().getString(R.string.button_follow));
                    button.setBackground(getContext().getDrawable(R.drawable.button_2));
                    add = false;
                }
                followAction(postUser.getUser().getUserId(), add);
            } else {
                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
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
}
