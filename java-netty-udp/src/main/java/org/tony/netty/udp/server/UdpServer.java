package org.tony.netty.udp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.upd
 */
public class UdpServer {

    public void run(Integer port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            //由于为UDP协议，需要使用NioDatagramChannel来创建，随后设置Socket参数支持广播
            //相比于TCP通信，UDP不存在客户端和服务端的实际连接，因此不需要为连接设置handler，对于服务端只需要设置启动辅助类的handler即可
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpServerHandler());
            bootstrap.bind(port).sync().channel().closeFuture().await();
        }finally {
            group.shutdownGracefully();
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
        new UdpServer().run(port);
    }

}
