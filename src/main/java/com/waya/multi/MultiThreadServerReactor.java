package com.waya.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程，reactor 模式  nio
 * 反应器
 *
 * @author BoJan
 * @version v 1.0
 * @date 2020/6/17 11:35
 */
public class MultiThreadServerReactor {
    ServerSocketChannel serverSocketChannel;
    AtomicInteger next = new AtomicInteger(0);
    //选择器集合， 引入多个选择器
    Selector[] selectors = new Selector[2];
    SubReactor[] subReactors = null;

    MultiThreadServerReactor() throws IOException {
        // 初始化多个选择器
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(80));
        serverSocketChannel.configureBlocking(false);
        // 第一个选择器， 负责监听新连接事件
        SelectionKey register = serverSocketChannel.register(selectors[0], SelectionKey.OP_ACCEPT);
        //绑定Handler：attach新连接监控handler处理器到SelectionKey（选择键）
        register.attach(new AcceptorHandler());

        //第一个子反应器，一子反应器负责一个选择器
        SubReactor subReactor1 = new SubReactor(selectors[0]);
        //第二个子反应器，一子反应器负责一个选择器
        SubReactor subReactor2 = new SubReactor(selectors[1]);
        subReactors = new SubReactor[]{subReactor1, subReactor2};
    }

    private void startService() {
        // 一子反应器对应一个线程
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }


    //子反应器
    class SubReactor implements Runnable {
        // 每个子反应器负责一个选择器的查询和选择
        Selector selector;

        public SubReactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        // 反应器负责dispatch接收到的事件
                        dispatch(iterator.next());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void dispatch(SelectionKey sk) {
            Runnable handler = (Runnable) sk.attachment();
            //调用之前attach绑定到选择键的handler处理器对象
            if (handler != null) {
                handler.run();
            }
        }
    }

    // Handler:新连接处理器
    class AcceptorHandler implements Runnable {
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
//                    new MultiThreadEchoHandler(selectors[next.get()], channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (next.incrementAndGet() == selectors.length) {
                next.set(0);
            }
        }
    }
}


