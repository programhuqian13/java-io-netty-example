package org.tony.netty.marshalling.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.marshalling.req.SubscribeReq;
import org.tony.netty.marshalling.resp.SubscribeResp;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.marshalling.server
 */
public class SubscribeServerHandler extends ChannelHandlerAdapter {

    public SubscribeServerHandler() {
    }

    private SubscribeResp resp(Integer id){
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setDesc("Netty book order success, 3 days later,sent to the designated address");
        subscribeResp.setRespCode(0);
        subscribeResp.setSubReqID(id);
        return subscribeResp;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq subscribeReq = (SubscribeReq) msg;
        if("Tony".equalsIgnoreCase(subscribeReq.getUserName())){
            System.out.println("Service accept client subscribe req : [" + subscribeReq.toString() +"]");
            ctx.writeAndFlush(resp(subscribeReq.getSubReqID()));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
