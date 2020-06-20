package com.waya.netty;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * TODO
 *
 * @author BoJan
 * @version v 1.0
 * @date 2020/6/18 10:30
 */
public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {
//    AbstractChannel
    Logger log = Logger.getLogger("NettyDiscardHandler");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        log.info("接收到消息： 丢弃");
        while (in.isReadable()) {
            System.out.print((char) in.readByte());
        }
        System.out.println();
        ReferenceCountUtil.release(msg);

    }
}
