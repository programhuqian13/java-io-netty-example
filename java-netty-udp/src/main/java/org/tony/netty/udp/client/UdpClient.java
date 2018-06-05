package org.tony.netty.udp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.udp.client
 */
public class UdpClient {

    public void run(Integer port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());
            Channel channel = bootstrap.bind(0).sync().channel();
            //进行广播UDP消息
            channel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语字典查询？", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255",port))).sync();
            if(!channel.closeFuture().await(15000)){
                System.out.println("查询超时！");
            }
        }finally {
            group.shutdownGracefully();
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
        new UdpClient().run(port);
    }
}
