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
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityImageMethodSelectBinding;
import com.program.moist.fragment.UserFragment;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

import static com.program.moist.base.AppConst.TAG;

public class ImageMethodSelectActivity extends BaseActivity {

    private ActivityImageMethodSelectBinding activityImageMethodSelectBinding;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageMethodSelectBinding = ActivityImageMethodSelectBinding.inflate(LayoutInflater.from(this));
        Integer type = (Integer) getIntent().getExtras().get(AppConst.Base.image_type);
        imagePath = type.equals(AppConst.Base.background) ?
                AppConst.User.user_avatar_path + SharedUtil.getString(App.context, AppConst.User.user_background, "") :
                AppConst.User.user_background_path + SharedUtil.getString(App.context, AppConst.User.user_avatar, "");
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

                                } else {
                                    ToastUtil.showToastShort("请给予相关权限");
                                }
                            });
                    break;
                case R.id.ic_close:
                    startActivity(new Intent(ImageMethodSelectActivity.this, MainActivity.class));
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
                }
            } else {
                ToastUtil.showToastShort("未获得照片");
            }
        }
    }
}