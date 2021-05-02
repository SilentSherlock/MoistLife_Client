package com.program.moist.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityImageMethodSelectBinding;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.InnerFileUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import static com.program.moist.base.AppConst.TAG;

public class ImageMethodSelectActivity extends BaseActivity {

    private ActivityImageMethodSelectBinding activityImageMethodSelectBinding;
    private String imagePath;
    private Integer imageType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageMethodSelectBinding = ActivityImageMethodSelectBinding.inflate(LayoutInflater.from(this));
        imageType= (Integer) getIntent().getExtras().get(AppConst.Base.image_type);
        imagePath = imageType.equals(AppConst.Base.background) ?
                AppConst.User.user_background_path + SharedUtil.getString(App.context, AppConst.User.user_background, "") :
                AppConst.User.user_avatar_path + SharedUtil.getString(App.context, AppConst.User.user_avatar, "");
        setContentView(activityImageMethodSelectBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: imagePath" + imagePath);
        ImageLoaderManager.loadImage(App.context, imagePath, activityImageMethodSelectBinding.currentImage);
    }

    @Override
    protected void eventBind() {
        ClickListener clickListener = new ClickListener();
        activityImageMethodSelectBinding.gallery.setOnClickListener(clickListener);
        activityImageMethodSelectBinding.imageCamera.setOnClickListener(clickListener);
        activityImageMethodSelectBinding.icClose.setOnClickListener(clickListener);
        activityImageMethodSelectBinding.imageSave.setOnClickListener(clickListener);
    }

    class ClickListener implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: click event");
            RxPermissions rxPermissions = new RxPermissions(ImageMethodSelectActivity.this);
            switch (v.getId()) {
                case R.id.gallery:
                    rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    App.imagePicker.setMultiMode(false);
                                    startActivityForResult(new Intent(ImageMethodSelectActivity.this, ImageGridActivity.class), 100);
                                } else {
                                    ToastUtil.showToastShort("请给予相关权限");
                                }
                            });
                    break;
                case R.id.camera:
                    rxPermissions.request(Manifest.permission.CAMERA)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    ToastUtil.showToastShort("相机功能有待完善，请用相册里的相机试试~");
                                } else {
                                    ToastUtil.showToastShort("请给予相关权限");
                                }
                            });
                    break;
                case R.id.ic_close:
                    startActivity(new Intent(ImageMethodSelectActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.image_save:
                    String exName = InnerFileUtil.saveFile(imagePath,
                            imageType.equals(AppConst.Base.avatar) ? AppConst.User.user_avatar_path : AppConst.User.user_background_path,
                            String.valueOf(App.getUserInfo().getUserId()));
                    if (exName != null && !exName.equals("")) {
                        SharedUtil.setString(App.context,
                                imageType.equals(AppConst.Base.avatar) ? AppConst.User.user_avatar : AppConst.User.user_background,
                                App.getUserInfo().getUserId() + exName);
                        uploadPhoto(imageType.equals(AppConst.Base.avatar) ?
                                AppConst.User.user_avatar_path + SharedUtil.getString(App.context, AppConst.User.user_avatar, "") :
                                AppConst.User.user_background_path + SharedUtil.getString(App.context, AppConst.User.user_background, ""));
                    } else {
                        ToastUtil.showToastShort("保存失败");
                        Log.i(TAG, "onClick: imageSave failed");
                    }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItems != null && imageItems.size() > 0) {
                    ImageLoaderManager.loadImage(App.context, imageItems.get(0).path, activityImageMethodSelectBinding.currentImage);
                    ToastUtil.showToastShort("获得照片" + imageItems.size() + "张");
                    imagePath = imageItems.get(0).path;
                } else {
                    ToastUtil.showToastShort("未获得照片");
                }
            } else {
                ToastUtil.showToastShort("未获得照片");
            }
        }
    }

    private void uploadPhoto(String imagePath) {
        OkGo.<Result>post(AppConst.User.addImage)
                .params("image", new File(imagePath))
                .params("userId", App.getUserInfo().getUserId())
                .params("type", imageType)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            ToastUtil.showToastShort("保存成功");
                        }
                    }
                });
    }
}