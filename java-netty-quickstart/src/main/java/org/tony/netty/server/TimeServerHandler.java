package org.tony.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * @Description TimeServerHandler继承自ChannelHandlerAdapter，它用于对网络事件进行读写操作，
 *
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/15
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.server
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg; //进行类型转换，将msg转换为byteBuf对象
        byte [] req = new byte[byteBuf.readableBytes()];//readableBytes方法可以获取缓冲区可读的字节数，根据可读的字节数创建数组
        byteBuf.readBytes(req);//readBytes方法将缓冲区中的字节数组复制到新创建的byte数组中
        String body = new String(req,"UTF-8");  //获取请求的消息
        System.out.println("The time server receive order : " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);  //异步发送应答消息给客户端
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将消息发送队列中的消息写入到SocketChannel中发送给对方
        //netty的write方法并不是直接将消息写入SocketChannel中，调用write方法只是把待发送的消息方法缓冲区数组中，
        //再通过调用flush方法，将发送到缓冲区中的消息全部写到SocketChannel中。
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
