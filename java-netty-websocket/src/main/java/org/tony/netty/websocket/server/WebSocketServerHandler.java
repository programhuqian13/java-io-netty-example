package org.tony.netty.websocket.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/22
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.websocket.server
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class.getName());

    private WebSocketServerHandshaker handshaker = null;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        //传统的HTTP接入
        if(message instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext,(FullHttpRequest)message);
        }
        //websocket接入
        else if(message instanceof WebSocketFrame){
            handleWebSocketFrame(channelHandlerContext,(WebSocketFrame)message);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) {
        //判断是否是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(channelHandlerContext.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        //判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            channelHandlerContext.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //本例程仅支持文本消息，不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not suppert",frame.getClass().getName()));
        }

        //返回应答消息
        String request = ((TextWebSocketFrame)frame).text();
        if(logger.isLoggable(Level.FINE)){
            logger.fine(String.format("%s received %s",channelHandlerContext.channel(),request));
        }

        channelHandlerContext.channel().write(new TextWebSocketFrame(request +
                " , 欢迎使用Netty Websocket 服务,现在时刻："
                + new Date().toString()));
    }

    private void handleHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) {
        //如果HTTP解码失败，返回HTTP异常
        if(!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(channelHandlerContext,req,new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //创建握手响应返回
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket",
                        null,false);
        handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(channelHandlerContext.channel());
        }else{
            handshaker.handshake(channelHandlerContext.channel(),req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext channelHandlerContext,
                                  FullHttpRequest req, DefaultFullHttpResponse res) {
        //返回应答给客户端
        if(res.getStatus().code() != 200){
            ByteBuf byteBuf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(byteBuf);
            byteBuf.release();
            setContentLength(res,res.content().readableBytes());
        }

        //如果是非Keep-Alive，关闭连接
        ChannelFuture f = channelHandlerContext.channel().writeAndFlush(res);
        if(!isKeepAlive(req) || res.getStatus().code() != 200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
