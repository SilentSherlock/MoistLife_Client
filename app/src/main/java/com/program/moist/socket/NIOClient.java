package com.program.moist.socket;

import android.util.Log;

import com.program.moist.base.AppConst;
import com.program.moist.entity.item.Message;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/12
 * Description: 客户端，在项目用作消息的发出方
 */
public class NIOClient implements Runnable, NIOPort{

    private final static String local = "localhost";
    private final static int defaultPort = 27149;

    private String host;
    private int port;
    private ByteBuffer bufferReader;
    private ByteBuffer bufferWriter;
    private Selector selector;
    private Charset charset;
    private SocketChannel client;
    private String msg;

    public static void connectAndSend(Message message, Integer port) {
        NIOClient nioClient;
        if (message.getSender() == null || port == null) {
            nioClient = new NIOClient(local, defaultPort, message.getContent() + AppConst.divide_2 + message.getSenderId());
        } else {
            nioClient = new NIOClient(message.getSender(), port, message.getContent());
        }
        new Thread(nioClient).start();
    }

    public NIOClient(String host, int port, int capacity, Charset charset, String msg) {
        this.host = host;
        this.port = port;
        bufferReader = ByteBuffer.allocate(capacity);
        bufferWriter = ByteBuffer.allocate(capacity);
        this.charset = charset;
        this.msg = msg;

        try {
            this.selector = Selector.open();
            this.client = SocketChannel.open();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            Log.e(TAG, "NIOClient: init", e);
        }

    }

    public NIOClient(String host, Integer port, String msg) {
        this(host, port, 4096, StandardCharsets.UTF_8, msg);
    }

    @Override
    public void handle(SelectionKey key) throws IOException {
        if (key.isConnectable()) {
            SocketChannel server = (SocketChannel) key.channel();
            if (server.isConnectionPending()) {
                server.finishConnect();
            }
            server.register(selector, SelectionKey.OP_READ);
            send(server, bufferWriter, msg + AppConst.divide_2 + AppConst.getFormatDate(null));
        } else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            System.out.println(receive(client, bufferReader));
            close(selector);
        }
    }

    @Override
    public void send(SelectableChannel channel, Buffer buffer, String msg) throws IOException {
        SocketChannel socketChannel = (SocketChannel) channel;
        ByteBuffer byteBuffer = (ByteBuffer) buffer;

        byteBuffer.clear();
        byteBuffer.put(charset.encode(msg));//写入编码后的信息
        byteBuffer.flip();//调整指针位置，由写入转为读取
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);//从缓冲中读取，向通道写入
        }
    }

    @Override
    public String receive(SelectableChannel channel, Buffer buffer) throws IOException {
        StringBuilder builder = new StringBuilder();
        SocketChannel socketChannel = (SocketChannel) channel;
        ByteBuffer byteBuffer = (ByteBuffer) buffer;

        byteBuffer.clear();
        while ((socketChannel.read(byteBuffer)) != 0) {//将通道中的字符读到缓存中
            buffer.flip();//调整指针位置，由写入变为读取
            builder.append(charset.decode(byteBuffer));
        }
        return builder.toString();
    }

    @Override
    public void close(Closeable closeable) {
        try {
            closeable.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            //连接消息接收方
            client.connect(new InetSocketAddress(host, port));
            while (selector.isOpen()) {
                selector.select();
                //System.out.println("client:" + selector.keys().size());
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    handle(key);
                }
                keys.clear();//清除处理过的事件
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(client);
            close(selector);
        }

        Log.i(TAG, "run: client 消息发送结束");
    }

}
