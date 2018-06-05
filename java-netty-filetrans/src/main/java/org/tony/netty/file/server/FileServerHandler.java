package org.tony.netty.file.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.file.server
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String>{

    private static final String CR = System.getProperty("line.separator");

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        File file = new File(msg);
        //对文件进行合法性校验
        if(file.exists()){
            if(!file.isFile()){
                channelHandlerContext.writeAndFlush("not a file : " + file + CR);
                return;
            }
            channelHandlerContext.write(file + " " + file.length() + CR);
            //以只读的方式打开
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg,"r");
            //进行文件传输 第一个参数：文件通道，用于对文件进行读写操作  2.文件操作的指针位置，读取或者写入的起始点 3.操作的总字节数
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(),0,randomAccessFile.length());
            //实现文件的发送
            channelHandlerContext.write(region);
            channelHandlerContext.writeAndFlush(CR);
            randomAccessFile.close();
        }else {
            channelHandlerContext.writeAndFlush("File not found: " + file + CR);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
