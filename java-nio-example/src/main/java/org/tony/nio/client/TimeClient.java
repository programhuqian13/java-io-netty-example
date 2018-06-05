package org.tony.nio.client;

/**
 * @Description NIO客户端实现
 * @Version 1.0
 * @Author tony
 * @Date 2018/5/10
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.nio.client
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if(args !=null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        new Thread(new TimeClientHandler("127.0.0.1",port),"TimeClient-001").start();
    }

}
