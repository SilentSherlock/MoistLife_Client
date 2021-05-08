package com.program.moist.utils;

import java.util.UUID;

/**
 * Author: SilentSherlock
 * Date: 2021/5/8
 * Description: 生成唯一性的key
 */
public class TokenUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
