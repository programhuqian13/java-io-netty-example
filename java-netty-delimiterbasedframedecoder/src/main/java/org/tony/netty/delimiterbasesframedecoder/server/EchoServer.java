package org.tony.netty.delimiterbasesframedecoder.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/16
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.delimiterbasesframedecoder.server
 */
public class EchoServer {

    public void bind(Integer port) throws InterruptedException {
        EventLoopGroup groups = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(groups,workGroup)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //创建一个分隔符缓冲对象
                            ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
                            //DelimiterBasedFrameDecoder有多个构造方法，我们传递两个参数
                            // 一个是1024表示单条消息的最大长度，当达到消息的长度仍然没有查找到分隔符就会抛出TooLongFrameException异常
                            //防止由于异常码流缺失分隔符导致的内存溢出，这是netty解码器的可靠性保护
                            //另一个参数就是分隔符的缓冲对象
                           // 由于DelimiterBasedFrameDecoder自动对请求消息进行了解码后续ChannelHandler接受到的msg就是一个完整的消息包
                            //StringDecoder会将ByteBuf解码成字符串对象
                            //EchoServerHandler接受到的msg消息就是解码后的字符串对象

                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,byteBuf)); //如果注释掉这行代码就会发生TCP粘包问题

                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //绑定成功，同步等待成功
            ChannelFuture f = serverBootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }finally {
            //释放资源
            groups.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Integer port = 8080;

        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }

        new EchoServer().bind(port);
    }
}
