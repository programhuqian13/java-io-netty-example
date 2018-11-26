package org.tony.netty.google.protobuf.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.google.protobuf.req.SubscribeReqProto;
import org.tony.netty.google.protobuf.resp.SubscribeRespProto;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.google.protobuf.client
 */
public class SubscribeClientHandler extends ChannelHandlerAdapter {

    public SubscribeClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0;i < 10;i ++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReqProto.SubscribeReq subReq(int i) {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setAddress("ShangHai PuDongXinQu");
        builder.setSubReqID(i);
        builder.setProductName("Netty book for protoBuf");
        builder.setUserName("Lilinfeng");
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
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
