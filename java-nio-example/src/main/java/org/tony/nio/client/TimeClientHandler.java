package org.tony.nio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/10
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.nio.client
 */
public class TimeClientHandler implements Runnable{

    private String host;

    private int port;

    private Selector selector;

    private SocketChannel socketChannel;

    private volatile boolean stop;

    public TimeClientHandler(String host,int port){
        this.host = host == null ? "127.0.0.1":host;
        this.port = port;
        try {
            selector = Selector.open();  //
            socketChannel = SocketChannel.open();  //打开socketChannel，绑定客户端本地地址
            socketChannel.configureBlocking(false);  //socketChannel设置为非阻塞模式
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();  //进行相应的连接
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    }catch (Exception e){
                        key.cancel();
                        if(key.channel() != null){
                            key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()){
            //判断是否连接成功
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            if(selectionKey.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.exit(1);  //结束失败  进程结束
                }
            }
            if(selectionKey.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte [] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("Now is : " + body);
                    this.stop = true;
                }else if(readBytes < 0){
                    selectionKey.cancel();
                    sc.close();
                }else{
                    //读到0字节 忽略
                }
            }
        }
    }

    private void doConnect() throws IOException {
        //如果直接连接成功，则注册到多复用器上，发送请求消息，读应答
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {

        byte [] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(req.length);
        byteBuffer.put(req);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        //hasRemaining()方法对发送结果判断，如果缓冲区中的消息全部发送完成
        if(!byteBuffer.hasRemaining()){
            System.out.println("Send order 2 server success");
        }

    }
}
