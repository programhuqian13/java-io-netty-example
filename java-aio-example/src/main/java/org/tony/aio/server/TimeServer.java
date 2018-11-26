package org.tony.aio.server;

/**
 * @Description AIO时间服务器服务端
 * @Version 1.0
 * @Date 2018/5/14
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.aio.server
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }

        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(asyncTimeServerHandler,"AIO-AsyncTimeServerHandler-001").start();

    }

}
