package org.tony.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description netty的初使用  时间服务器
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/15
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.server
 */
public class TimeServer {

    public void bind(Integer port) throws InterruptedException {
        //配置服务端的NIO
        /***
         * EventLoopGroup是线程组，它包含了一组NIO线程，专门用于网络时间的处理，实际上它们就是Reactor线程组
         * 创建两个group的原因为：
         *      1.一个用于服务器接受客户端的连接
         *      2.另一个用于处理SocketChannel的网络读写
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /***
             * ServerBootstrap：netty用于启动的辅助类，目的是降低服务端的开发复杂度
             */
            ServerBootstrap b = new ServerBootstrap();
            /***
             *  将EventLoopGroup当参数传递给ServerBootstrap中
             *  channel：设置channel，NioServerSocketChannel的功能对应NIO库中的ServerSocketChannel
             *  option：配置TCP相应的参数
             *  childHandler：主要用于处理网络I/O事件
             */
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            /***
             * ChannelFuture：主要用于异步操作的通知回调
             */
            ChannelFuture f = b.bind(port).sync();  //sync方法进行阻塞，等待服务端链路关闭之后mian函数退出处理
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }finally {
            //退出 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        Integer port = 8080;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
                port = 8080;  //默认值
            }
        }
        new TimeServer().bind(port);
    }

}
