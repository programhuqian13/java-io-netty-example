package org.tony.netty.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @Description 描述
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/26
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty
 */
public class TimeServerHandler implements Runnable{

    private Socket socket;

    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }

    /***
     * 线程启动
     */
    @Override
    public void run() {
        BufferedReader in  = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(),true);
            String currentTime = null;
            String body = null;
            while(true){
                body = in.readLine();  //读取一行，如果读到了输入流的尾部，则返回为null 退出循环
                if(body == null){
                    break;
                }
                System.out.println("The time server receive order: "  + body);
                //进行内容判断
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                out.println(currentTime); //通过pringln函数发送给客户端，停止循环
            }
        } catch (IOException e) {
            //释放相应的资源并被虚拟机回收掉
            if(in != null){
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(out != null){
                out.close();
                out = null;
            }
            if(this.socket != null){
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
