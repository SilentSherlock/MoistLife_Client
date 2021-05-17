package com.program.moist.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.program.moist.R;
import com.program.moist.activity.ImageMethodSelectActivity;
import com.program.moist.activity.LoginActivity;
import com.program.moist.activity.ProfileActivity;
import com.program.moist.activity.UserActivity;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.DialogCameraBinding;
import com.program.moist.databinding.FragmentUserBinding;
import com.program.moist.entity.User;
import com.program.moist.utils.FTPUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

import static com.program.moist.base.AppConst.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends BaseFragment {

    private FragmentUserBinding fragmentUserBinding;
    private DialogCameraBinding dialogCameraBinding;
    private BottomSheetDialog bottomSheetDialog;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentUserBinding = FragmentUserBinding.inflate(inflater, container, false);
        dialogCameraBinding = DialogCameraBinding.inflate(inflater, container, false);

        initView();
        eventBind();
        return fragmentUserBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    /**
     * 向ftp请求数据为子线程，主线程继续操作
     * 导致图片更新不及时
     * 需要设置定时器
     *
     * 请求图片已改为OSS对象存储，避免子线程问题
     */
    @Override
    protected void initView() {
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        User user = App.getUserInfo();
        if (user == null) {
            ToastUtil.showToastShort("用户尚未登录");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + user.getUserAvatar(), fragmentUserBinding.userAvatar);
            ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + user.getUserBackground(), fragmentUserBinding.userBackground);
            fragmentUserBinding.userName.setText(user.getUserName());
        }

    }

    @Override
    protected void eventBind() {
        ClickListener clickListener = new ClickListener();
        fragmentUserBinding.userAvatarBadge.setOnClickListener(clickListener);
        for (int i = 0;i < dialogCameraBinding.layoutDialog.getChildCount();i++) {
            dialogCameraBinding.layoutDialog.getChildAt(i).setOnClickListener(clickListener);
        }
        fragmentUserBinding.userInfo.setOnClickListener(clickListener);
        fragmentUserBinding.userFav.setOnClickListener(clickListener);
        fragmentUserBinding.userPost.setOnClickListener(clickListener);
        fragmentUserBinding.userFollow.setOnClickListener(clickListener);
    }

    class ClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.user_avatar_badge:
                    showDialog();
                    break;
                case R.id.dialog_cancel:
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.dialog_change_avatar:
                    intent.setClass(getContext(), ImageMethodSelectActivity.class);
                    intent.putExtra(AppConst.Base.image_type, AppConst.Base.avatar);
                    startActivity(intent);
                    dismissDialog();
                    break;
                case R.id.dialog_change_background:
                    intent.setClass(getContext(), ImageMethodSelectActivity.class);
                    intent.putExtra(AppConst.Base.image_type, AppConst.Base.background);
                    startActivity(intent);
                    dismissDialog();
                    break;
                case R.id.dialog_change_profile:
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                    dismissDialog();
                    break;
                case R.id.user_info:
                    toUser(AppConst.Base.kind_info);
                    break;
                case R.id.user_fav:
                    toUser(AppConst.Base.kind_fav);
                    break;
                case R.id.user_post:
                    toUser(AppConst.Base.kind_post);
                    break;
                case R.id.user_follow:
                    toUser(AppConst.Base.kind_follow);
                    break;
            }
        }
    }

    private void toUser(Integer kindType) {
        Intent intent1 = new Intent(getContext(), UserActivity.class);
        intent1.putExtra(AppConst.Base.kind, kindType);
        startActivity(intent1);
    }
    /**
     * 显示底部对话框，提供头像背景图更换
     */
    private void showDialog() {
        bottomSheetDialog.setContentView(dialogCameraBinding.getRoot());
        bottomSheetDialog.show();
    }

    private void dismissDialog() {
        bottomSheetDialog.dismiss();
        ((ViewGroup) dialogCameraBinding.getRoot().getParent()).removeView(dialogCameraBinding.getRoot());
    }
}