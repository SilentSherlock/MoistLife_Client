package com.program.moist.utils;

import com.program.moist.entity.item.Message;

/**
 * Author: SilentSherlock
 * Date: 2021/5/12
 * Description: server接受到信息的回调
 */
public interface MessageCallback {
    void onSuccess(Message message);
}
