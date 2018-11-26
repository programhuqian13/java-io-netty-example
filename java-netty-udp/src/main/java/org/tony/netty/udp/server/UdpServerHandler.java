package org.tony.netty.udp.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ThreadLocalRandom;


/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.udp.server
 */
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    //谚语列表
    private static final String [] DICTIONARY = {"只要功夫深，铁柱磨成针","旧时王谢堂前燕，飞入寻常百姓家","一寸光阴一寸金，寸金难买寸光阴"};

    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    //对UDP进行了封装DatagramPacket
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        //将packet的内容转换为字符串
        String req = datagramPacket.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        //对请求消息进行合法性判断
        if("谚语字典查询？".equals(req)){
            channelHandlerContext.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语查询结果：" + nextQuote(),CharsetUtil.UTF_8),datagramPacket.sender()
                    ));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
