package org.tony.netty.stickpackage.exception.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/15
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.stickpackage.exception.server
 */
public class ChildServerHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new TimeServerHandler());
    }
}
