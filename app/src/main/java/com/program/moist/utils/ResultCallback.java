package com.program.moist.utils;

import android.util.Log;

import com.lzy.okgo.callback.AbsCallback;

import okhttp3.ResponseBody;

/**
 * Author: SilentSherlock
 * Date: 2021/4/20
 * Description: describe the class
 */
public abstract class ResultCallback extends AbsCallback<Result> {

    @Override
    public Result convertResponse(okhttp3.Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) {
            Log.i("ResultCallback", "response body is null");
            return null;
        }
        return GsonUtil.fromJson(body.string(), Result.class);
    }

}
