package com.program.moist.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: sharePreference统一管理，保存用户数据简单数据
 */
public class SharedUtil {

    public static final String pref_name = "config";

    public static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
