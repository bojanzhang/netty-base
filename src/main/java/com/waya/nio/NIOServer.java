package com.waya.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import io.netty.util.concurrent.Future;

/**
 * TODO
 *
 * @author BoJan
 * @version v 1.0
 * @date 2020/6/16 17:24
 */
public class NIOServer {
    private Charset charset = Charset.forName("UTF-8");
    private Logger logger = Logger.getLogger("NIOServer");


    /**
     * 内部类，服务端保存的客户端对象，对应一个客户端文件
     *
     * @param
     * @return
     */
    static class Client {
        //文件名
        String fileName;
        //文件长度
        Long fileLength;
        //开始传输时间
        Long startTime;
        //客户端地址
        InetSocketAddress remoteAddress;
        //输出文件通道
        FileChannel fileOutChannel;
    }

    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    // 使用Map保存每个文件传输，当OP_READ可读时， 根据通道找到对应的对象
    Map<SelectableChannel, Client> clientMap = new HashMap<>();

    public void startServer() throws IOException {
//        1,获取选择器
        Selector selector = Selector.open();
// 2， 获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
//        3，设置非阻塞模式
        serverSocketChannel.configureBlocking(false);
//        4,绑定连接
        InetSocketAddress address = new InetSocketAddress(80);
        socket.bind(address);

//        5,将通道注册到选择器上， 并注册IO事件为“接收新连接”
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("server is listening ....");
//        6，获取感兴趣的IO 就绪事件
        while (selector.select() > 0) {
//            7,获取选择键集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
//                8，获取单个的选择键，并处理
                SelectionKey key = iterator.next();
            }

        }

    }

}
