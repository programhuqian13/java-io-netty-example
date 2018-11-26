package org.tony.netty.agreement.message.auth;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.agreement.message.Header;
import org.tony.netty.agreement.message.NettyMessage;

import java.awt.*;

/**
 * @Description 请求认证
 * @Version 1.0
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message.auth
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //如果是握手应答消息，需要判断是否认证成功
        if(message.getHeader() != null && message.getHeader().getType() == (byte)2){
            byte loginResult = (byte) message.getBody();
            if(loginResult != (byte)0){
                //握手失败，关闭连接
                ctx.close();
            }else{
                System.out.println("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte)1);
        message.setHeader(header);
        return message;
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
