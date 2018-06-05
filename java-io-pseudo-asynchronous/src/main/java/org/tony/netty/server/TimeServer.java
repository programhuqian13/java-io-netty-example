package org.tony.netty.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description 伪异步IO实现
 *                  为了解决同步阻塞I/O面临的问题：一个链路需要创建一个线程
 *                   后端通过一个线程池来处理多个客户端的请求接入，形成客户端个数M:线程池最大线程数N的比例关系，其中M可以远远大于N
 *                   通过线程池可以灵活的调配线程资源，设置线程的最大值，防止由于海量并发接入导致线程耗尽
 *
 *                   伪异步I/O通信框架采用了线程池实现，因此避免了为每一个请求都创建一个独立线程造成的线程资源耗尽问题。
 *                   但是由于低层还是通信依然采用的是阻塞式模型，因此无法从根本上解决问题。
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/26
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.server
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }

        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50,1000); //创建I/O任务线程池
            while(true){
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        }finally {
            if(server != null){
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }

}
