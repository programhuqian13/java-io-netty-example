package org.tony.netty.agreement.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.tony.netty.agreement.message.utils.MarshallingCodeCFactory;
import org.tony.netty.agreement.message.utils.NettyMarshallingEncoder;

import java.util.List;
import java.util.Map;

/**
 * @Description 消息编码器 NettyMessageEncoder
 * @Version 1.0
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage>{

    private NettyMarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder(){
        this.marshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, List<Object> out) throws Exception {
        if(msg == null || msg.getHeader() == null){
            throw new Exception("the encode messsage is null");
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(msg.getHeader().getCrcCode());
        byteBuf.writeInt(msg.getHeader().getLength());
        byteBuf.writeLong(msg.getHeader().getSessionId());
        byteBuf.writeByte(msg.getHeader().getType());
        byteBuf.writeByte(msg.getHeader().getPriority());
        byteBuf.writeInt(msg.getHeader().getAttachment().size());
        String key = null;
        byte [] keyArray = null;
        Object value = null;
        for(Map.Entry<String,Object> param : msg.getHeader().getAttachment().entrySet()){
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            byteBuf.writeInt(keyArray.length);
            byteBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(channelHandlerContext,value,byteBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if(msg.getBody() != null){
            marshallingEncoder.encode(channelHandlerContext,msg.getBody(),byteBuf);
        }else{
            byteBuf.setInt(4,byteBuf.readableBytes());
        }

    }
}
