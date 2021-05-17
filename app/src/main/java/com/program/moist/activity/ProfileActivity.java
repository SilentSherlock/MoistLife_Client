package com.program.moist.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityProfileBinding;
import com.program.moist.entity.User;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.regex.Pattern;

/**
 * Author: SilentSherlock
 * Date: 2021/5/16
 * Description: describe the class
 */
public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding activityProfileBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(activityProfileBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        ImageLoaderManager.loadImageWeb(App.context, App.getUserInfo().getUserAvatar(), activityProfileBinding.profileUserAvatar);
        activityProfileBinding.profileUserName.setText(App.getUserInfo().getUserName());
        activityProfileBinding.profileArea.setText(
                App.getUserInfo().getLocation() != null ? App.getUserInfo().getLocation() : ""
        );
        activityProfileBinding.profilePhone.setText(
                App.getUserInfo().getPhoneNumber() != null ? App.getUserInfo().getPhoneNumber() : ""
        );
        activityProfileBinding.profileEmail.setText(
                App.getUserInfo().getEmail() != null ? App.getUserInfo().getEmail() : ""
        );
        activityProfileBinding.profileIdentifyNumber.setText(
                App.getUserInfo().getIdentifyNumber() != null ? App.getUserInfo().getIdentifyNumber() : ""
        );
    }



    @Override
    protected void eventBind() {
        activityProfileBinding.profileSave.setOnClickListener(v -> {
            String userName = activityProfileBinding.profileUserName.getText().toString();
            String location = activityProfileBinding.profileArea.getText().toString();
            String phone = activityProfileBinding.profilePhone.getText().toString();
            String email = activityProfileBinding.profileEmail.getText().toString();
            String identify = activityProfileBinding.profileIdentifyNumber.getText().toString();

            if (!"".equals(userName) && !"".equals(location) && !"".equals(phone) && !"".equals(email) && !"".equals(identify)) {
                if (Pattern.matches(AppConst.Base.number_format, phone) && Pattern.matches(AppConst.Base.email_format, email) && Pattern.matches(AppConst.Base.identify_number_format, identify)) {
                    User user = App.getUserInfo();
                    user.setUserName(userName);
                    user.setLocation(location);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    user.setIdentifyNumber(identify);

                    App.setUserInfo(user);
                    updateUser();
                } else {
                    ToastUtil.showToastShort("请检查关键信息哦~");
                }
            } else {
                ToastUtil.showToastShort("请填好完整信息哦~");
            }
        });
    }

    private void updateUser() {
        OkGo.<Result>post(AppConst.User.updateUser)
                .params("userName", App.getUserInfo().getUserName())
                .params("phoneNumber", App.getUserInfo().getPhoneNumber())
                .params("email", App.getUserInfo().getEmail())
                .params("password", App.getUserInfo().getPassword())
                .params("identifyNumber", App.getUserInfo().getIdentifyNumber())
                .params("userKind", App.getUserInfo().getUserKind() != null ? App.getUserInfo().getUserKind() : 1)
                .params("userAvatar", App.getUserInfo().getUserAvatar() != null ? App.getUserInfo().getUserAvatar() : "")
                .params("userBackground", App.getUserInfo().getUserBackground() != null ? App.getUserInfo().getUserBackground() : "")
                .params("userLocation", App.getUserInfo().getLocation() != null ? App.getUserInfo().getLocation() : "")
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            ToastUtil.showToastShort("更新成功~");
                            finish();
                        }
                    }
                });
    }
}
