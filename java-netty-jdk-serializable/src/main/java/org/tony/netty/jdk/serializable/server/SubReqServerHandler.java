package org.tony.netty.jdk.serializable.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.jdk.serializable.req.SubscribeReq;
import org.tony.netty.jdk.serializable.resp.SubscribeResp;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.jdk.serializable.server
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //经过解码器handler ObjectDecoder的解码，SubReqServerHandler接受到的请求消息已经被自动解码为SubscribeReq的对象
        SubscribeReq req = (SubscribeReq) msg;
        /**
         * 订单的合法性校验
         */
        if("Lilinfeng".equalsIgnoreCase(req.getUserName())){
            System.out.println("Service accept client subscribe req : " + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeResp resp(Integer subReqID) {
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setSubReqID(subReqID);
        subscribeResp.setRespCode(0);
        subscribeResp.setDesc("Netty book order successd ,3day later ,sent to the designated address");
        return subscribeResp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();  //发生异常链路关闭
    }
}
