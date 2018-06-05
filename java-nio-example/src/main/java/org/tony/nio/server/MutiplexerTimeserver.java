package org.tony.nio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description NIO 多路复用类
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/27
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.nio.server
 */
public class MutiplexerTimeserver implements Runnable{

    private Selector selector; // 定义一个多路复用器

    private ServerSocketChannel serverSocketChannel; //创建一个ServerSocket管道

    private volatile boolean stop;

    public MutiplexerTimeserver(int port){
        try {
            //初始化NIO的多路复用器和SocketChannel对象
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);  //设置为异步非阻塞
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);  //设置backLog大小为1024
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //注册select管道
            System.out.println("The time server is start in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                //遍历selector，设置休眠时间为1s
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();  //返回就绪状态下的selectionKeys集合
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);  //进行网络的异步读写操作
                    }catch (Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if(selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            //处理新接入的请求消息
            if(key.isAcceptable()){
                //请求一个新的连接（处理新接入的消息） 根据selectionKey的操作位进行判断即可获知网络事件的类型
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept(); //通过accpt接受客户端的连接请求并创建SocketChannel实例
                //上面的操作相当于完成了TCP的三次握手，TCP物理链路正式建立
                sc.configureBlocking(false);  //设置为非阻塞
                sc.register(selector,SelectionKey.OP_READ);
            }
            if(key.isReadable()){
                //读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);  //开辟一个1K的缓冲区
                int readBytes = sc.read(readBuffer); //读取请求码流 因为设置为异步非阻塞的，因此它的read是非阻塞的
                /***
                 * 返回值 > 0:读到了字节，对字节进行编解码
                 * 返回值等于0：没有读取到字节，属于正常场景 忽略
                 * 返回值 < 0:链路已经关闭，需要关闭socketChannel释放资源。
                 */
                if(readBytes > 0){
                    readBuffer.flip();  //将缓冲区当前的limit设置为position，position设置为0 为后续对缓冲区的读取操作
                    byte [] bytes = new byte[readBuffer.remaining()]; //根据缓冲区的可读的字节个数创建字节数组
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("The time server receive order:" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(sc,currentTime);
                }else if(readBytes < 0){
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                }else{
                    //读到0字节  不进行处理
                }
            }

        }

    }

    /**
     * 将应达消息异步发送给客户端
     * @param sc
     * @param currentTime
     * @throws IOException
     */
    private void doWrite(SocketChannel sc, String currentTime) throws IOException {

        if(currentTime != null && currentTime.trim().length() > 0){
            byte [] bytes = currentTime.getBytes();
            ByteBuffer wirteBuffer = ByteBuffer.allocate(bytes.length);
            wirteBuffer.put(bytes);  //将字节码复制到缓冲区中
            wirteBuffer.flip(); //
            sc.write(wirteBuffer); //将缓冲区的字节数组发送出去
        }

    }
}
