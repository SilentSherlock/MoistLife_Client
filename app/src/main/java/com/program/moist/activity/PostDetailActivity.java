package com.program.moist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.CommentAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityPostDetailBinding;
import com.program.moist.entity.Comment;
import com.program.moist.entity.User;
import com.program.moist.entity.item.CommentUser;
import com.program.moist.entity.item.PostUser;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.Date;
import java.util.List;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class PostDetailActivity extends BaseActivity {

    private ActivityPostDetailBinding activityPostDetailBinding;
    private CommentAdapter commentAdapter;
    private PostUser postUser;
    private Integer toUserId;
    private String toUserName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPostDetailBinding = ActivityPostDetailBinding.inflate(LayoutInflater.from(this));
        setContentView(activityPostDetailBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        postUser = (PostUser) getIntent().getSerializableExtra("postUser");
        toUserId = postUser.getUser().getUserId();
        toUserName = postUser.getUser().getUserName();
        commentAdapter = new CommentAdapter(R.layout.item_comment_card);
        activityPostDetailBinding.postDetailCommentContent.setAdapter(commentAdapter);
        activityPostDetailBinding.postDetailCommentContent.setLayoutManager(new LinearLayoutManager(this));

        activityPostDetailBinding.postDetailUserName.setText(postUser.getUser().getUserName());
        activityPostDetailBinding.postDetailTitle.setText(postUser.getPost().getPostTitle());
        activityPostDetailBinding.postDetailDetail.setText(postUser.getPost().getDetail());

        activityPostDetailBinding.postDetailComment.setHint("回复 " + postUser.getUser().getUserName() + ":");

        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + postUser.getUser().getUserAvatar(), activityPostDetailBinding.postDetailUserAvatar);
        //加载帖子中的图片
        String image = postUser.getPost().getPostPictures();
        if (image != null && !image.equals("")) {
            String[] images = image.split(AppConst.divide_2);
            for (String s : images) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + s, imageView);
                activityPostDetailBinding.postDetailImages.addView(imageView);
            }
        }

        //加载评论
        getComment(postUser.getPost().getPostId());
    }

    @Override
    protected void eventBind() {
        //返回按钮事件
        activityPostDetailBinding.postDetailToolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        //关注事件
        activityPostDetailBinding.postDetailButton.setOnClickListener(v -> {
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
                followAction(postUser.getUser().getUserId(), add);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        //实时更新评论的回复人
        commentAdapter.setOnItemClickListener((adapter, view, position) -> {
            LinearLayout linearLayout = (LinearLayout) view;
            TextView hideName = (TextView) linearLayout.getChildAt(0);
            TextView hideId = (TextView) linearLayout.getChildAt(1);
            toUserName = hideName.getText().toString();
            activityPostDetailBinding.postDetailComment.setHint("回复 " + toUserName + ":");
            toUserId = Integer.parseInt(hideId.getText().toString());
        });

        //添加评论
        activityPostDetailBinding.postDetailSendComment.setOnClickListener(v -> {
            String text = activityPostDetailBinding.postDetailComment.getText().toString();
            if (!text.equals("")) {
                Comment comment = new Comment();
                comment.setFromUserId(App.getUserInfo().getUserId());
                comment.setToUserId(toUserId);
                comment.setComTime(new Date());
                comment.setContent(text);
                comment.setPostId(postUser.getPost().getPostId());
                comment.setComKind(1);

                if (toUserId.equals(postUser.getUser().getUserId())) {
                    commentAdapter.addData(0, CommentUser.createByDefault(App.getUserInfo(), comment));
                } else {
                    commentAdapter.addData(0, CommentUser.createByAll(App.getUserInfo(), comment, new User(toUserName)));
                }
                ToastUtil.showToastShort("评论成功~");
                activityPostDetailBinding.postDetailComment.setText("");
            } else {
                ToastUtil.showToastShort("评论不能为空哦~");
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


    //获取post对应的comment
    private void getComment(Integer postId) {
        OkGo.<Result>post(AppConst.Post.getCommentByPostId)
                .params("postId", postId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            List<Comment> comments = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Post.comments)),
                                    new TypeToken<List<Comment>>(){}.getType()
                            );
                            if (comments != null && comments.size() != 0) {
                                for (Comment comment : comments) {
                                    getUser(comment.getFromUserId(), comment);
                                }
                            }
                        }
                    }
                });
    }

    private void getUser(Integer userId, Comment comment) {
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
                            commentAdapter.addData(CommentUser.createByDefault(user, comment));
                        } else {
                            Log.i(TAG, "onSuccess: 未获得用户");
                        }
                    }
                });
    }
}
