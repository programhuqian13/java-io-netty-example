package org.tony.netty.google.protobuf.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.google.protobuf.req.SubscribeReqProto;
import org.tony.netty.google.protobuf.resp.SubscribeRespProto;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.google.protobuf.server
 */
@ChannelHandler.Sharable
public class SubscribeReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * ProtobufDecoder已经对消息进行了自动解码，因此接受到的订购请求消息可以直接使用
         * 由于使用了ProtobufEncoder所以不需要对消息进行手工编码
         */
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if("Lilinfeng".equalsIgnoreCase(req.getUserName())){
            System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    public SubscribeRespProto.SubscribeResp resp(Integer id){
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(id);
        builder.setDesc("Netty book order success, 3 days later,sent to the designated address");
        builder.setRespCode(0);
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();  //发生异常关闭链路
    }
}
