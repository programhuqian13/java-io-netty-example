package org.tony.netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/21
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.http.server
 */
public class HttpFileServer {

    private static final String DEFULT_URL = "/src/org/tony/netty/";

    public void run(Integer port,final String URL) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加HTTP请求消息解码器
                            socketChannel.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                            //添加HttpObjectAggregator解码器 作用为将多个消息转换为单一的FullHttpRequest或者FullHttpResponse 原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
                            socketChannel.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            //添加一个HTTP的相应编码器，对HTTP相应的消息进行编码
                            socketChannel.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            //ChunkedWriteHandler主要作用是支持异步发送大的码流，但不占过多的内存，防止java内存溢出异常
                            socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast(new HttpFileServerHandler(URL));
                        }
                    });

            ChannelFuture future = serverBootstrap.bind("127.0.0.1",port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址为：http://127.0.0.1:" + port + URL);
            future.channel().closeFuture().sync();
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
        String URL = DEFULT_URL;
        if(args.length > 1){
            URL = args[1];
        }
        new HttpFileServer().run(port,URL);
    }

}
