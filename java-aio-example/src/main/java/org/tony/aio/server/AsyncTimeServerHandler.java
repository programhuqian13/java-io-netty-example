package org.tony.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/14
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.aio.server
 */
public class AsyncTimeServerHandler implements Runnable {

    private Integer port;

    CountDownLatch latch;

    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(Integer port){
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();  //异步的服务端通道
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));  //绑定监听的端口，绑定成功打印成功语句
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);  //在完成一组正在执行的操作之前，允许当前的线程一直阻塞 在这里阻塞防止服务端执行完成退出。
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
    }

}
