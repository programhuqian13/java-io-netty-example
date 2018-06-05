package org.tony.netty.jdk.serializable.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.jdk.serializable.req.SubscribeReq;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.jdk.serializable.client
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    public SubReqClientHandler() {
    }

    /***
     * 当链路激活的时候发送10条请求消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0 ; i < 10; i++){
            ctx.write(subReq(i));
        }
        //最后一次性发送给服务端
        ctx.flush();
    }

    private SubscribeReq subReq(Integer i){
        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setAddress("上海市浦东新区浦东大道栖霞路");
        subscribeReq.setPhoneNumber("XXXXXXXXXXXXXXX");
        subscribeReq.setProductName("Netty 权威指南");
        subscribeReq.setSubReqID(i);
        subscribeReq.setUserName("Lilinfeng");
        return subscribeReq;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //由于对象解码器已经将请求应答消息进行了自动解码，因此这里接受到的消息已经是解码成功后的订购应答消息
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
