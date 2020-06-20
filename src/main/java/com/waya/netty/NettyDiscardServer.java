package com.waya.netty;

import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty 基础， 服务端
 *
 * @author BoJan
 * @version v 1.0
 * @date 2020/6/18 9:59
 */
public class NettyDiscardServer {
    private final int serverPort;
    Logger  log = Logger.getLogger("NettyDiscardServer");

    ServerBootstrap serverBootstrap = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void runServer() {

        //1,创建反应器线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 2,设置反应器线程组
            serverBootstrap.group(bossGroup, workGroup);
            // 3, 设置nio 类型的通道
            serverBootstrap.channel(NioServerSocketChannel.class);
            //4， 设置监听端口
            serverBootstrap.localAddress(serverPort);
            // 5， 设置通道参数
            serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // 6 装配子通道流水线
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                // 有连接到达时会创建一个通道
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 向子通道流水线添加一个handler
                    ch.pipeline().addLast(new NettyDiscardHandler());
                }
            });
            // 7,绑定服务器， 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture future = serverBootstrap.bind().sync();
            log.info(" 服务器启动成功，监听端口: "+
                    future.channel().localAddress());
            // 8, 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭异步任务结束
            ChannelFuture channelFuture = future.channel().closeFuture();
            channelFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 9，关闭EventLoopGroup
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new NettyDiscardServer(80).runServer();
    }

}
