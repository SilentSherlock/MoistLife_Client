package com.program.moist.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityEditBinding;
import com.program.moist.entity.Category;
import com.program.moist.entity.Post;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class EditActivity extends BaseActivity {

    private ActivityEditBinding activityEditBinding;
    private int imagePointer = 0;
    private HashMap<String, String> imageMap;//imagePath->image exName
    private Category category;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditBinding = ActivityEditBinding.inflate(LayoutInflater.from(this));
        setContentView(activityEditBinding.getRoot());
        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        if (!App.isLogin()) {
            ToastUtil.showToastShort("尚未登录");
            startActivity(new Intent(this, LoginActivity.class));
        }
        category = (Category) getIntent().getSerializableExtra("category");
        imageMap = new HashMap<>();
        if (category == null) {
            activityEditBinding.editToolBar.setTitle("发布帖子");
            activityEditBinding.editInfoNeed.setVisibility(View.GONE);
        } else {
            activityEditBinding.editToolBar.setTitle("发布" + category.getCateName());
        }

        invisibleImageChoose();
        visibleImage(imagePointer++);
    }

    @Override
    protected void eventBind() {
        activityEditBinding.editToolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        activityEditBinding.editRelease.setOnClickListener(v -> {
            if (!validate()) {
                ToastUtil.showToastShort("请填写完整信息");
            } else {
                StringBuilder pictures = new StringBuilder();
                if (!imageMap.isEmpty()) {
                    for (String key : imageMap.keySet()) {
                        String newName = TokenUtil.getUUID();
                        uploadPhoto(key, newName, imageMap.get(key));
                        pictures.append(newName).append(imageMap.get(key)).append(AppConst.divide_2);
                    }
                }
                Log.i(TAG, "eventBind: 开始发送消息");
                if (category == null) {
                    uploadPost(pictures.toString());
                } else {
                    uploadInfo(pictures.toString());
                }
            }
        });
    }

    private boolean validate() {
        Editable titleText = activityEditBinding.editTitle.getText();
        Editable detailText = activityEditBinding.editDetail.getText();

        boolean postNeed = titleText != null && detailText != null &&
                !titleText.toString().equals("") && !detailText.toString().equals("");
        if (category == null) return postNeed;
        Editable areaText = activityEditBinding.editArea.getText();
        Editable priceText = activityEditBinding.editPrice.getText();
        return postNeed && areaText != null && priceText != null &&
                !areaText.toString().equals("") && !priceText.toString().equals("");
    }
    //先上传图片
    private void uploadPost(String pictures) {
        if (pictures == null) pictures = "";
        OkGo.<Result>post(AppConst.Post.addPost)
                .params("postTitle", Objects.requireNonNull(activityEditBinding.editTitle.getText()).toString())
                .params("userId", Objects.requireNonNull(App.getUserInfo()).getUserId())
                .params("topicId", AppConst.getRandomNumber(200))
                .params("detail", Objects.requireNonNull(activityEditBinding.editDetail.getText()).toString())
                .params("postTime", AppConst.getFormatDate(null))
                .params("postState", 1)
                .params("postPictures", pictures)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            ToastUtil.showToastShort("发布成功");
                            finish();
                        }
                    }
                });
    }

    private void uploadInfo(String pictures) {
        if (pictures == null) pictures = "";
        OkGo.<Result>post(AppConst.Info.addInfo)
                .params("infoTitle", Objects.requireNonNull(activityEditBinding.editTitle.getText()).toString())
                .params("userId", Objects.requireNonNull(App.getUserInfo()).getUserId())
                .params("cateId", category.getCateId())
                .params("detail", Objects.requireNonNull(activityEditBinding.editDetail.getText()).toString())
                .params("infoTime", AppConst.getFormatDate(null))
                .params("updateTime", AppConst.getFormatDate(null))
                .params("area", Objects.requireNonNull(activityEditBinding.editArea.getText()).toString())
                .params("price", Objects.requireNonNull(activityEditBinding.editPrice.getText()).toString())
                .params("infoPictures", pictures)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            ToastUtil.showToastShort("发布成功");
                            finish();
                        }
                    }
                });
    }
    private void invisibleImageChoose() {
        for (int i = 0;i < activityEditBinding.editImageRoot.getChildCount();i++) {
            LinearLayout row = (LinearLayout) activityEditBinding.editImageRoot.getChildAt(i);
            for (int j = 0;j < row.getChildCount();j++) {
                ImageView item = (ImageView) row.getChildAt(j);
                item.setVisibility(View.INVISIBLE);
            }
        }
    }

    //imagePointer最大为8
    private void visibleImage(int imagePointer) {
        if (imagePointer > 8) {
            Log.i(TAG, "visibleImage: 图片指针数值出错 " + imagePointer);
        } else {
            int rows = activityEditBinding.editImageRoot.getChildCount();
            for (int i = 0;i < rows;i++) {
                LinearLayout row = (LinearLayout) activityEditBinding.editImageRoot.getChildAt(i);
                for (int j = 0;j < row.getChildCount();j++) {
                    int index = i * rows + j;
                    if (index == imagePointer) {
                        ImageView item = (ImageView) row.getChildAt(j);
                        item.setVisibility(View.VISIBLE);

                        item.setOnClickListener(v -> {
                            RxPermissions rxPermissions = new RxPermissions(EditActivity.this);
                            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    .subscribe(aBoolean -> {
                                        if (aBoolean) {
                                            App.imagePicker.setMultiMode(false);
                                            startActivityForResult(new Intent(EditActivity.this, ImageGridActivity.class), 100);
                                        } else {
                                            ToastUtil.showToastShort("请给予相关权限");
                                        }
                                    });
                        });
                        return;
                    }
                }
            }
        }
    }

    private void loadImageToPoint(String imagePath) {
        int rows = activityEditBinding.editImageRoot.getChildCount();
        for (int i = 0;i < rows;i++) {
            LinearLayout row = (LinearLayout) activityEditBinding.editImageRoot.getChildAt(i);
            for (int j = 0;j < row.getChildCount();j++) {
                int index = i * rows + j;
                if (index == imagePointer) {
                    ImageView item = (ImageView) row.getChildAt(j);
                    item.setVisibility(View.VISIBLE);
                    ImageLoaderManager.loadImage(App.context, imagePath, item);
                    visibleImage(imagePointer++);
                    return;
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
                    loadImageToPoint(imageItems.get(0).path);
                    ToastUtil.showToastShort("获得照片" + imageItems.size() + "张");
                    String imageURL = imageItems.get(0).path;
                    String exName = imageURL.substring(imageURL.lastIndexOf("."));
                    imageMap.put(imageURL, exName);
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
    private void uploadPhoto(String imageURL, String newName, String exName) {
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
}
