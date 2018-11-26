package org.tony.netty.marshalling.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.tony.netty.marshalling.req.SubscribeReq;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.marshalling.client
 */
public class SubscribeClientHandler extends ChannelHandlerAdapter {

    public SubscribeClientHandler() {
    }

    /***
     * 激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0;i<10;i++){
            ctx.write(req(i));
        }
        ctx.flush();
    }

    /***
     * 读消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
    }

    /***
     * 读完成之后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private SubscribeReq req(Integer id){
        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setAddress("ShangHai PuDingXinQu");
        subscribeReq.setProductName("Netty book for marshalling");
        subscribeReq.setUserName("Tony");
        subscribeReq.setSubReqID(id);
        return subscribeReq;
    }

    /***
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
