package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityRegisterBinding;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.regex.Pattern;

/**
 * Author: SilentSherlock
 * Date: 2021/4/16
 * Description: describe the class
 */
public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding activityRegisterBinding;
    private CountDownTimer validateTimer;
    private int accountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this));
        setContentView(activityRegisterBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {
        validateTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                activityRegisterBinding.getEditValidateCode.setText((int) (millisUntilFinished / 1000) + "秒后重新获取");
            }

            @Override
            public void onFinish() {
                //定时器完成，重新绑定点击事件
                activityRegisterBinding.getEditValidateCode.setOnClickListener(new RegisterClickListener());
                activityRegisterBinding.getEditValidateCode.setText("重新获取");
            }
        };
    }

    /**
     * 绑定页面中的需要处理的事件
     */
    @Override
    protected void eventBind() {
        RegisterClickListener clickListener = new RegisterClickListener();

        activityRegisterBinding.registerToLogin.setOnClickListener(clickListener);
        activityRegisterBinding.icClose.setOnClickListener(clickListener);
        activityRegisterBinding.getEditValidateCode.setOnClickListener(clickListener);
        activityRegisterBinding.register.setOnClickListener(clickListener);
    }


    /**
     * 点击事件监听类
     */
    class RegisterClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.register_to_login:
                    intent.setClass(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.ic_close:
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.get_edit_validate_code:
                    //先校验account是否存在, 再发送验证码
                    String s = activityRegisterBinding.editAccount.getText().toString();
                    if (!s.equals("")) {
                        if (Pattern.matches(AppConst.Base.number_format, s) || Pattern.matches(AppConst.Base.email_format, s)) {
                            int type = Pattern.matches(AppConst.Base.number_format, s) ? AppConst.Base.type_number : AppConst.Base.type_email;
                            OkGo.<Result>post(AppConst.User.check)
                                    .params("msg", s)
                                    .params("type", type)
                                    .execute(new ResultCallback() {
                                        @Override
                                        public void onSuccess(Response<Result> response) {
                                            Result result = response.body();
                                            ToastUtil.showToastShort(result.getDescription());
                                            //发送验证码并启动定时器
                                            if (result.getStatus() == Status.SUCCESS) {
                                                OkGo.<Result>get(AppConst.User.accountValidate)
                                                        .params("account", activityRegisterBinding.editAccount.getText().toString())
                                                        .params("type", type)
                                                        .execute(new ResultCallback() {
                                                            @Override
                                                            public void onSuccess(Response<Result> response) {
                                                                Result result = response.body();
                                                                if (result.getStatus() == Status.SUCCESS) {
                                                                    ToastUtil.showToastShort("验证码已发送");
                                                                    activityRegisterBinding.getEditValidateCode.setOnClickListener(null);
                                                                    accountType = type;
                                                                    validateTimer.start();
                                                                } else {
                                                                    ToastUtil.showToastShort("出错啦");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
                            ToastUtil.showToastLong(AppConst.Base.number + "或" + AppConst.Base.email + AppConst.Base.format_wrong);
                        }
                    }
                    break;
                case R.id.register:
                    String account = activityRegisterBinding.editAccount.getText().toString();
                    String password = activityRegisterBinding.editPassword.getText().toString();
                    String validateCode = activityRegisterBinding.editValidateCode.getText().toString();

                    if (account.equals("") || password.equals("") || validateCode.equals("")) {
                        ToastUtil.showToastShort("请填入完整信息");
                    } else {
                        OkGo.<Result>post(AppConst.User.register)
                                .params("account", account)
                                .params("password", password)
                                .params("validateCode", validateCode)
                                .params("type", accountType)
                                .execute(new ResultCallback() {
                                    @Override
                                    public void onSuccess(Response<Result> response) {
                                        Result result = response.body();
                                        if (result.getStatus() == Status.SUCCESS) {
                                            ToastUtil.showToastShort("注册成功");
                                            SharedUtil.setString(App.context, AppConst.Base.login_token, (String) result.getResultMap().get(AppConst.Base.login_token));
                                            SharedUtil.setString(App.context, AppConst.User.user, GsonUtil.toJson(result.getResultMap().get(AppConst.User.user)));
                                            intent.setClass(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            ToastUtil.showToastShort(result.getDescription());
                                        }
                                    }
                                });
                    }
                    break;
            }
        }
    }
}
