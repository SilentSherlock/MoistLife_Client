package com.program.moist.socket;

import android.util.Log;

import com.program.moist.base.AppConst;
import com.program.moist.entity.item.Message;
import com.program.moist.utils.MessageCallback;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/12
 * Description: 服务端，在项目中用作消息的接收方
 */
public class NIOServer implements NIOPort, Runnable{

    private final static String local = "localhost";
    private final static int defaultPort = 27149;

    private String host;
    private int port;
    private ByteBuffer bufferReader;
    private ByteBuffer bufferWriter;
    private Selector selector;
    private Charset charset;
    private MessageCallback messageCallback;

    private static boolean run = true;
    public NIOServer(String host, int port, int capacity, Charset charset, MessageCallback messageCallback) {
        this.host = host;
        this.port = port;
        bufferReader = ByteBuffer.allocate(capacity);
        bufferWriter = ByteBuffer.allocate(capacity);
        this.charset = charset;
        this.messageCallback = messageCallback;
    }

    public NIOServer(String host, int port, MessageCallback messageCallback) {
        this(host, port, 4096, StandardCharsets.UTF_8, messageCallback);
    }

    public static void start(String host, Integer port, MessageCallback messageCallback) {
        NIOServer nioServer;
        if (host == null || port == null) {
            nioServer = new NIOServer(local, defaultPort, messageCallback);
        } else {
            nioServer = new NIOServer(host, port, messageCallback);
        }
        new Thread(nioServer, "NIOServer").start();
    }

    public static void stop() {
        run = false;
    }
    @Override
    public void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            String msg = receive(client, bufferReader);
            String host = client.socket().getInetAddress().getHostAddress();
            send(client, bufferWriter, "服务端接收到来自" + host + "的消息");
            String[] contents = msg.split(AppConst.divide_2);
            /*content+senderId+date*/
            messageCallback.onSuccess(Message.createByAll(host, contents[0], contents[2], contents[1]));
            close(client);
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

    /**
     * 循环监听端口，有没有消息发送过来
     */
    @Override
    public void run() {
        ServerSocketChannel channel;
        try {
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(host, port));
            channel.configureBlocking(false);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (run) {
                selector.select();
                System.out.println("server:" + selector.keys().size());
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    handle(key);
                }
                keys.clear();
            }
            Log.i(TAG, "run: NIOServer 停止运行");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
