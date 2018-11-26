package org.tony.netty.http.server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/21
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.http.server
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    private final String url;

    public HttpFileServerHandler(String url){
        this.url = url;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        //对http请求消息的解码结果进行判断
        if(!fullHttpRequest.getDecoderResult().isSuccess()){
            //如果解码失败  返回400错误
            sendError(channelHandlerContext,BAD_REQUEST);
            return;
        }
        //对请求方法进行判断  如果不等于get 将返回405错误
        if(fullHttpRequest.getMethod() != GET){
            sendError(channelHandlerContext,METHOD_NOT_ALLOWED);
            return ;
        }
        final String uri = fullHttpRequest.getUri();
        //对请求的url进行包装
        final String path = sanitizeUri(uri);
        if(path == null){
            sendError(channelHandlerContext,FORBIDDEN);
            return;
        }
        File file = new File(path);
        if(file.isHidden() || !file.exists()){
            sendError(channelHandlerContext,NOT_FOUND);
            return;
        }
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                sendListing(channelHandlerContext,file);
            }else{
                sendRedirect(channelHandlerContext,uri + "/");
            }
            return ;
        }
        if(!file.isFile()){
            sendError(channelHandlerContext,FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");  //以只读的方式打开文件
        }catch (FileNotFoundException f){
            sendError(channelHandlerContext,NOT_FOUND);
            return;
        }
        //获取文件的长度
        Long fileLength = randomAccessFile.length();
        //构造http请求成功的应答
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //设置contenttype和content length
        setContentLength(response,fileLength);
        setContentTypeHeader(response,file);
        //判断是否是Keep-Alive 如果是就添加KEEP_ALIVE
        if(isKeepAlive(fullHttpRequest)){
            //
            response.headers().set(CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
        }
        //发送相应消息
        channelHandlerContext.write(response);
        //通过netty的ChunkedFile对象直接将文件写入到发送缓冲区中
        ChannelFuture sendFileFuture;
        sendFileFuture = channelHandlerContext.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),channelHandlerContext.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                System.out.println("Transfer complate.");
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long progress, long total) throws Exception {
                if(total < 0){
                    System.err.println("Transfer progress: " + progress);
                }else{
                    System.err.println("Transfer progress:" + progress + "/" + total);
                }
            }
        });
        //LastHttpContent.EMPTY_LAST_CONTENT发送到缓冲去中，表示所有的消息体已经发送完成，同时调用flush方法将之前在发送缓冲区的消息刷新到SocketChannel中发送给对方
        ChannelFuture lastContentFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果非keep-alive的，最后一包消息发送完成之后，服务端要主动关闭连接
        if(!isKeepAlive(fullHttpRequest)){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].");

    /***
     * 对请求的url进行包装
     * @param uri
     * @return
     */
    private String sanitizeUri(String uri) {
        try{
            //对URL进行解码
            uri = URLDecoder.decode(uri,"UTF-8");
        }catch (UnsupportedEncodingException e){
            try {
                uri = URLDecoder.decode(uri,"ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        //进行URL的合法合法性判断
        if(!uri.startsWith(url)){
            return null;
        }

        if(!uri.startsWith("/")){
            return null;
        }

        //将硬编码的文件路径分隔符替换成本地系统的文件路径分隔符
        uri = uri.replace("/",File.separator);
        //对新的url进行第二次合法校验 如果校验失败之间赶回null
        if(uri.contains(File.separator + '.') || uri.contains('.' + File.separator)
                || uri.startsWith(".") || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        //对文件进行拼接
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private void sendListing(ChannelHandlerContext channelHandlerContext, File file) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        StringBuilder builder = new StringBuilder();
        String dirPath = file.getPath();
        builder.append("<!DOCTYPE html>\r\n");
        builder.append("<html><head><title>");
        builder.append(dirPath);
        builder.append(" 目录：");
        builder.append("</title></head><body>\r\n");
        builder.append("<h3>");
        builder.append(dirPath).append(" 目录：");
        builder.append("</h3>\r\n");
        builder.append("<ul>");
        builder.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
        for(File file1 : file.listFiles()){
            if(file1.isHidden() || !file1.canRead()){
                continue;
            }
            String name = file1.getName();
            if(!ALLOWED_FILE_NAME.matcher(name).matches()){
                continue;
            }
            builder.append("<li>链接:<a href=\"");
            builder.append(name);
            builder.append("\">");
            builder.append(name);
            builder.append("</a></li>\r\n");
        }
        builder.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendRedirect(ChannelHandlerContext channelHandlerContext, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FOUND);
        response.headers().set(LOCATION,newUri);
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext channelHandlerContext,HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure:   " + status.toString() + "\r\n",CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/plain;charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void setContentTypeHeader(HttpResponse response, File file) {

        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,mimetypesFileTypeMap.getContentType(file.getPath()));

    }

}
