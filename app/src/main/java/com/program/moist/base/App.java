package com.program.moist.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.program.moist.entity.User;
import com.program.moist.entity.item.Message;
import com.program.moist.socket.NIOClient;
import com.program.moist.socket.NIOServer;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.InnerFileUtil;
import com.program.moist.utils.MessageCallback;
import com.program.moist.utils.SharedUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static com.program.moist.base.AppConst.TAG;

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
    public static LinkedBlockingQueue<Message> messageQueue;//用来存放正在聊天的消息
    public static LinkedBlockingQueue<Message> receiveQueue;//存放接收到的消息，由MessageFragment中线程消费，用来通知新消息到来

    @Override
    public void onCreate() {
        super.onCreate();
        activities = new ArrayList<>();
        app = this;
        context = this.getApplicationContext();

        initOkGo();
        initImagePicker();
        initIM();
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

    /**
     * 初始化SOCKET服务端进行通信
     * 获取到消息后，检测取消息的线程是否存在，也就是接收者是否打开了聊天窗口
     * 存在则放到消息队列中，并持久化到本地
     * 不存在则直接持久化
     * 如果存放消息文件第一次创建，则通知MessageFragment中更新UI线程，新增对话框
     */
    public void initIM() {
        messageQueue = new LinkedBlockingQueue<>();
        receiveQueue = new LinkedBlockingQueue<>();
        NIOServer.start(null, null, message -> {
            Log.i(TAG, "onSuccess: host " + message.getSender() + " msg " + message.getContent());
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread.getName().equals(message.getSender())) {
                    try {
                        messageQueue.put(message);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "onSuccess: messageQueue", e);
                    }

                }
            }
            boolean first = InnerFileUtil.saveObjectToFile(message, AppConst.Base.chat_dir, message.getSender() + ".txt");
            try {
                receiveQueue.put(message);
            } catch (InterruptedException e) {
                Log.e(TAG, "initIM: receiveQueue", e);
            }

        });
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

    public static boolean isLogin() {
        return getUserInfo() != null;
    }
    public static void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }

        SharedUtil.setString(App.context, AppConst.Base.login_token, "");
        SharedUtil.setString(App.context, AppConst.User.user, "");
    }

}
