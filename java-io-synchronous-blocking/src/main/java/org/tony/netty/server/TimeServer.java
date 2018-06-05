package org.tony.netty.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description 同步阻塞式I/O创建
 *              BIO主要的问题在于每当有一个新的客户端请求接入时，服务端必须创建一个新的线程处理新接入的客户端链路
 *              一个线程只能处理一个客户端连接
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/26
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;  //设置端口号
        if(args != null && args.length > 0){
            try {
                port = Integer.valueOf(args[0]);  //如果没有相应的运行参数，端口为默认的8080，如果有就为设置的值
            }catch (NumberFormatException e){

            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);  //创建一个serverSocket，如果端口合法并且没有占用，服务器监听成功
            System.out.println("the time server is start in port:" + port);
            Socket socket = null;
            //通过一个无限循环来监听客户端的连接，如果没有将主线程阻塞在accept()上
            while(true){
                socket = server.accept();
                //TimeServerHandler实现runnable接口的实现run的类  实现线程运行
                new Thread(new TimeServerHandler(socket)).start();
            }
        }finally {
            if(server != null){
                System.out.println("The time server close'");
                server.close();
                server = null;
            }
        }
    }

}
