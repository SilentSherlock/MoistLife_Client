package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityLoginBinding;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.SharedUtil;
import com.program.moist.utils.Status;
import com.program.moist.utils.ToastUtil;

import java.util.regex.Pattern;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/4/15
 * Description: describe the class
 */
public class LoginActivity extends BaseActivity{

    private ActivityLoginBinding activityLoginBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(activityLoginBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void eventBind() {
        ClickListener clickListener = new ClickListener();
        activityLoginBinding.loginToRegister.setOnClickListener(clickListener);
        activityLoginBinding.icClose.setOnClickListener(clickListener);
        activityLoginBinding.login.setOnClickListener(clickListener);
    }

    class ClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_to_register:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();
                    break;
                case R.id.ic_close:
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.login:
                    String account = activityLoginBinding.loginAccount.getText().toString();
                    String password = activityLoginBinding.loginPassword.getText().toString();
                    Log.i(TAG, "onClick: account " + account + " password " + password);
                    if (account.equals("") || password.equals("")) {
                        ToastUtil.showToastShort("账户和密码均不能为空哦~");
                    } else if (!Pattern.matches(AppConst.Base.email_format, account) && !Pattern.matches(AppConst.Base.number_format, account)){
                        Log.i(TAG, "onClick: " + Pattern.matches(AppConst.Base.email_format, account));
                        Log.i(TAG, "onClick: " + Pattern.matches(AppConst.Base.number_format, account));
                        ToastUtil.showToastShort("账户格式不正确哦~");
                    } else {
                        Integer type = Pattern.matches(AppConst.Base.email_format, account) ? AppConst.Base.type_email : AppConst.Base.type_number;
                        login(account, password, type);
                    }
                    break;
            }
        }
    }

    private void login(String account, String password, Integer type) {
        OkGo.<Result>post(AppConst.User.login)
                .params("account", account)
                .params("password", password)
                .params("type", type)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            Log.i(TAG, "onSuccess: login_token " + result.getResultMap().get(AppConst.Base.login_token));
                            SharedUtil.setString(App.context, AppConst.Base.login_token, (String) result.getResultMap().get(AppConst.Base.login_token));
                            SharedUtil.setString(App.context, AppConst.User.user, GsonUtil.toJson(result.getResultMap().get(AppConst.User.user)));
                            App.setLoginToken(SharedUtil.getString(App.context, AppConst.Base.login_token, ""));

                            ToastUtil.showToastShort("登录成功");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else if (result.getStatus() == Status.FALSE) {
                            ToastUtil.showToastShort("账户或密码错误哦~");
                        } else if (result.getStatus() == Status.NOT_FOUND) {
                            ToastUtil.showToastShort("账户不存在呢，请先注册");
                            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        super.onError(response);
                        Log.i(TAG, "onError: " + response.code());
                    }
                });
    }
}
