package org.tony.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/22
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.websocket.server
 */
public class WebSocketServer {

    public void run(Integer port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //将请求和应答消息编码或者解码俄日HTTP消息
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            //将多个部分组合成一条完整的Http消息
                            pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                            //来向客户端发送HTML5文件，主要用于浏览器和服务器进行websocket通信
                            socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            //添加websocket服务端handler
                            pipeline.addLast(new WebSocketServerHandler());
                        }
                    });
            Channel channel = serverBootstrap.bind(port).sync().channel();
            System.out.println("Web socket server started at port " + port + ".");
            System.out.println("Open your brower and navigate to http://localhost:" + port + "/");
            channel.closeFuture().sync();
        }finally {
            //释放资源
            group.shutdownGracefully();
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

        new WebSocketServer().run(port);
    }

}
