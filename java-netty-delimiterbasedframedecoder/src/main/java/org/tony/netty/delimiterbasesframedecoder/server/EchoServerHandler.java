package org.tony.netty.delimiterbasesframedecoder.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/16
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.delimiterbasesframedecoder.server
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //直接将接受的消息打印出来
       String body = (String) msg;
        System.out.println("This is " + ++counter + " time receive client : [" + body + "]");
        body += "$_";//在消息的结尾追加"$_"分割符
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes()); //将原始消息返回给客户端
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();  //发生异常，关闭链路
    }
}
