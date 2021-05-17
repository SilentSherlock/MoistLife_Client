package com.program.moist.socket;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * Author: SilentSherlock
 * Date: 2021/5/12
 * Description: 客户端服务端通用接口
 */
public interface NIOPort {
    /**
     * 对触发的事件进行相应的处理
     * */
    void handle(SelectionKey key) throws IOException;
    /**
     * 从缓冲中读取字节流到通道中
     * */
    void send(SelectableChannel channel, Buffer buffer, String msg) throws IOException;
    /**
     * 从通道中读取字节流到缓冲中
     * */
    String receive(SelectableChannel channel, Buffer buffer) throws IOException;
    /**
     * 关闭一个流
     * */
    void close(Closeable closeable) throws IOException;
}
