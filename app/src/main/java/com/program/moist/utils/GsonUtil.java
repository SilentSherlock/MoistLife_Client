package com.program.moist.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: describe the class
 */
public class GsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static String toJson(Object object, Type type) {
        return gson.toJson(object, type);
    }

    public static <T> T fromJson(String str, Class<T> tClass) {
        return gson.fromJson(str, tClass);
    }

    public static <T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }
}
