package com.program.moist.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: describe the class
 */
public class GsonUtil {
    private static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gson = gsonBuilder.create();
    }

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
