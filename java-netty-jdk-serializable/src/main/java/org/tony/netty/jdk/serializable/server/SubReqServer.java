package org.tony.netty.jdk.serializable.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.jdk.serializable.server
 */
public class SubReqServer {

    public void bind(Integer port) throws InterruptedException {
        //创建服务端的NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group,workgroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            /***
                             * 首先创建一个ObjectDecoder，负责对实现Serializable的POJO对象进行解码
                             *      有多个构造函数，支持不同的ClassResolver
                             *  weakCachingConcurrentResolver创建线程安全的WeakReferenceMap对类加载器进行缓存
                             *  它支持多线程并发访问，当虚拟机内存不足时，会释放缓存中的内存，防止内存泄漏。
                             *    这里单个对象的序列化后的字节数组长度设置为1M
                             */
                            socketChannel.pipeline().addLast(new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            /***
                             * ObjectEncoder可以在消息发送的时候自动将实现Serializable的POJO对象进行编码，因此用户不需要手动序列化
                             * 只需要关注自己的业务逻辑即可
                             */
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                            socketChannel.pipeline().addLast(new SubReqServerHandler());
                        }
                    });
            //绑定端口，同步等待成功
            ChannelFuture f = serverBootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }finally {
            //释放资源
            group.shutdownGracefully();
            workgroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Integer port = 8080;
        if(args != null && args.length > 0){
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }

        new SubReqServer().bind(port);
    }

}
