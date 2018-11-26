package org.tony.netty.google.protobuf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.tony.netty.google.protobuf.req.SubscribeReqProto;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.google.protobuf.server
 */
public class SubscribeReqServer {

    public void bind(Integer port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup worksGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group,worksGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //ProtobufVarint32FrameDecoder主要用于半包处理
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            //添加ProtobufDecoder解码器 实际上就是要告诉ProtobufDecoder需要解码的目标类是什么否则仅仅从字节数组中是无法判断出要解码的目标类型信息。
                            socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.
                                    SubscribeReq.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new SubscribeReqServerHandler());
                        }
                    });
            //绑定端口 创建链路
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //等待客户端关闭链路
            future.channel().closeFuture().sync();
        }finally {
            //清理资源
            group.shutdownGracefully();
            worksGroup.shutdownGracefully();
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

        new SubscribeReqServer().bind(port);
    }

}
