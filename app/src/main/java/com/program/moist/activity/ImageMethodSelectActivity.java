package com.program.moist.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
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
import com.program.moist.entity.User;
import com.program.moist.entity.item.StsToken;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.OssUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;
import com.program.moist.utils.TokenUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Objects;

import static com.program.moist.base.AppConst.TAG;

public class ImageMethodSelectActivity extends BaseActivity {

    private ActivityImageMethodSelectBinding activityImageMethodSelectBinding;
    private String imageURL;
    private Integer imageType;
    private String exName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageMethodSelectBinding = ActivityImageMethodSelectBinding.inflate(LayoutInflater.from(this));
        imageType = (Integer) getIntent().getExtras().get(AppConst.Base.image_type);
        imageURL = imageType.equals(AppConst.Base.background) ?
                Objects.requireNonNull(App.getUserInfo()).getUserBackground() :
                Objects.requireNonNull(App.getUserInfo()).getUserAvatar();
        setContentView(activityImageMethodSelectBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: imagePath" + imageURL);
        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + imageURL, activityImageMethodSelectBinding.currentImage);
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
                    finish();
                    break;
                case R.id.image_save:
                    uploadPhoto(TokenUtil.getUUID(), exName);
                    finish();
                    break;
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
                    imageURL = imageItems.get(0).path;
                    exName = imageURL.substring(imageURL.lastIndexOf("."));
                } else {
                    ToastUtil.showToastShort("未获得照片");
                }
            } else {
                ToastUtil.showToastShort("未获得照片");
            }
        }
    }

    /**
     * 上传图片
     * @param newName 图片新的名字,使用UUID生成唯一性id
     * @param exName 扩展名
     */
    private void uploadPhoto(String newName, String exName) {
        OkGo.<Result>post(AppConst.User.getStsToken)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            StsToken stsToken = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.Base.sts_token)),
                                    new TypeToken<StsToken>(){}.getType()
                            );
                            Log.i(TAG, "onSuccess: stsToken" + stsToken.toString());
                            OssUtil.getInstance().uploadImage(
                                    App.context,
                                    stsToken.getAccessKeyId(),
                                    stsToken.getAccessKeySecret(),
                                    stsToken.getSecurityToken(),
                                    new OssUtil.OssUpCallback() {
                                        @Override
                                        public void onSuccess(String imgName, String imgUrl) {
                                            ToastUtil.showToastShort("保存成功");
                                            //删除旧的图片
                                            OssUtil.getInstance().deleteImage(
                                                    App.context,
                                                    stsToken.getAccessKeyId(),
                                                    stsToken.getAccessKeySecret(),
                                                    stsToken.getSecurityToken(),
                                                    AppConst.Base.avatar.equals(imageType) ?
                                                            Objects.requireNonNull(App.getUserInfo()).getUserAvatar() :
                                                            Objects.requireNonNull(App.getUserInfo()).getUserBackground()
                                            );
                                            updateUser(imgName);
                                        }

                                        @Override
                                        public void onFail(String message) {
                                            ToastUtil.showToastShort(message);
                                        }

                                        @Override
                                        public void onProgress(long progress, long totalSize) {

                                        }
                                    },
                                    newName + exName,
                                    imageURL
                            );
                        }
                    }
                });
    }

    private void updateUser(String imageName) {
        OkGo.<Result>post(AppConst.User.updateUserColumn)
                .params("column", AppConst.Base.avatar.equals(imageType) ? "user_avatar" : "user_background")
                .params("value", imageName)
                .params("userId", App.getUserInfo().getUserId())
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            User user = App.getUserInfo();
                            if (AppConst.Base.avatar.equals(imageType)) {
                                user.setUserAvatar(imageName);
                            } else {
                                user.setUserBackground(imageName);
                            }
                            App.setUserInfo(user);
                        }
                    }
                });
    }
}