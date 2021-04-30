package com.program.moist.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.program.moist.entity.User;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.SharedUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: 继承Application，添加全局变量，统一管理app
 */
public class App extends Application {

    public static List<Activity> activities;
    public static OkGo okGo;
    public static ImagePicker imagePicker;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static App app;


    @Override
    public void onCreate() {
        super.onCreate();
        activities = new ArrayList<>();
        app = this;
        context = this.getApplicationContext();

        initOkGo();
        initImagePicker();
    }

    /**
     * 初始化okGo
     */
    public void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //添加日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor("okGo");
        interceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setColorLevel(Level.INFO);

        builder.addInterceptor(interceptor);

        okGo = OkGo.getInstance()
                .init(app)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);

        setLoginToken(getLoginToken());
    }

    /**
     * 初始化ImagePicker
     */
    public void initImagePicker() {

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoaderManager());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(6);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }


    public static String getLoginToken() {
        return SharedUtil.getString(context, AppConst.Base.login_token, "");
    }

    public static void setLoginToken(String loginToken) {
        SharedUtil.setString(context, AppConst.Base.login_token, loginToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(AppConst.Base.login_token, loginToken);
        okGo.addCommonHeaders(httpHeaders);
    }

    public static void setUserInfo(User user) {
        String str = GsonUtil.toJson(user);
        SharedUtil.setString(context, AppConst.User.user, str);

    }

    public static User getUserInfo() {
        String userInfo = SharedUtil.getString(context, AppConst.User.user, "");
        if (userInfo.equals("")) return null;
        return GsonUtil.fromJson(userInfo, User.class);
    }

    public static void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
        SharedUtil.setString(App.context, AppConst.Base.login_token, "");
        SharedUtil.setString(App.context, AppConst.User.user, "");
    }
}
