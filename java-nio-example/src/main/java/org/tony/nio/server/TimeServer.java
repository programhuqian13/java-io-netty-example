package org.tony.nio.server;

/**
 * @Description NIO 同步非阻塞I/O
 *
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/26
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.nio.server
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

        MutiplexerTimeserver timeserver = new MutiplexerTimeserver(port);

        new Thread(timeserver,"NIO-MutiplexerTimeServer-001").start();

    }

}
