package org.tony.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/14
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.aio.server
 */
public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel channel;  //主要用于读取半包消息和发送应答

    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        if(this.channel == null){
            this.channel = channel;
        }
    }


    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();  //为后续从缓冲区读取数据做准备
        byte [] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body,"UTF-8");  //创建请求消息
            System.out.println("The time server receive order : " + req);
            //对请求消息进行判断
            String currntTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() :"BAD ORDER";
            doWrite(currntTime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 将消息发送给客户端
     * @param currntTime
     */
    private void doWrite(String currntTime) {
        //对当前时间进行合法性校验
        if(currntTime != null && currntTime.trim().length() > 0){
            byte [] bytes = (currntTime).getBytes();  //调用字符串的解码方法将应答消息编码成字节数组，然后将它复制到发送缓冲区writeBuffer中
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            //异步write方法
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    //如果没有发送完成，继续发送
                    if(attachment.hasRemaining()){
                        channel.write(attachment,attachment,this);
                    }
                }
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
//                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
