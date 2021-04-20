package com.program.moist.utils;

import android.content.Context;
import android.widget.Toast;

import com.program.moist.base.App;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: 统一管理Toast
 */
public class ToastUtil {

    private static Toast toast;

    /**
     * 短时间居下显示
     * @param msg
     */
    public static void showToastShort(String msg) {
        if (toast == null) {
            toast = Toast.makeText(App.context, msg, Toast.LENGTH_SHORT);
        } else toast.setText(msg);
        toast.show();
    }

    public static void showToastLong(String msg) {
        if (toast == null) {
            toast = Toast.makeText(App.context, msg, Toast.LENGTH_LONG);
        } else toast.setText(msg);
        toast.show();
    }

    /**
     * 其他线程中显示
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
