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
        fragmentUserBinding = FragmentUserBinding.inflate(inflater);
        dialogCameraBinding = DialogCameraBinding.inflate(inflater);

        initView();
        eventBind();
        return fragmentUserBinding.getRoot();
    }

    /**
     * 向ftp请求数据为子线程，主线程继续操作
     * 导致图片更新不及时
     * 需要设置定时器
     */
    @Override
    protected void initView() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        User user = App.getUserInfo();
        if (user == null) {
            ToastUtil.showToastShort("用户尚未登录");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            String avatar = SharedUtil.getString(App.context, AppConst.User.user_avatar, "");
            String background = SharedUtil.getString(App.context, AppConst.User.user_background, "");
            if (avatar.equals("") && user.getUserAvatar() != null && !user.getUserAvatar().equals("")) {
                FTPUtil.download(user.getUserAvatar(), AppConst.User.user_avatar_path, avatar);
                Log.i(TAG, "initView: avatar" + avatar);
                //SharedUtil.setString(App.context, AppConst.User.user_avatar, AppConst.User.user_avatar_path + avatar);
                File file = new File( AppConst.User.user_avatar_path + "5.png");
                Log.i(TAG, "initView: " + file.exists() + "\n" + file.getAbsolutePath() + "\n" + file.getPath() + App.context.getFilesDir().getPath());
                ImageLoaderManager.loadImage(App.context, file.getAbsolutePath(), fragmentUserBinding.userAvatar);
            }
            if (background.equals("") && user.getUserBackground() != null && !user.getUserBackground().equals("")) {
                FTPUtil.download(user.getUserBackground(), AppConst.User.user_background_path, background);
                ImageLoaderManager.loadImage(App.context, AppConst.User.user_background_path + background, fragmentUserBinding.userBackground);
            }
            //ImageLoaderManager.loadImage(App.context, AppConst.User.user_avatar_path + avatar, fragmentUserBinding.userAvatar);
            ImageLoaderManager.loadImage(App.context, AppConst.User.user_background_path + background, fragmentUserBinding.userBackground);
        }
    }

    @Override
    protected void eventBind() {
        ClickListener clickListener = new ClickListener();
        fragmentUserBinding.userAvatarBadge.setOnClickListener(clickListener);
        for (int i = 0;i < dialogCameraBinding.layoutDialog.getChildCount();i++) {
            dialogCameraBinding.layoutDialog.getChildAt(i).setOnClickListener(clickListener);
        }
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
                    break;
                case R.id.dialog_change_background:
                    intent.setClass(getContext(), ImageMethodSelectActivity.class);
                    intent.putExtra(AppConst.Base.image_type, AppConst.Base.background);
                    startActivity(intent);
                    break;
                case R.id.dialog_change_profile:
                    break;
            }
        }
    }

    /**
     * 显示底部对话框，提供头像背景图更换
     */
    private void showDialog() {
        bottomSheetDialog.setContentView(dialogCameraBinding.getRoot());
        bottomSheetDialog.show();
    }
}