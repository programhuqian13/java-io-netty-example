package org.tony.netty.agreement.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.tony.netty.agreement.message.utils.MarshallingCodeCFactory;
import org.tony.netty.agreement.message.utils.NettyMarshallingDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 消息解码器 NettyMessageDecoder
 * LengthFieldBasedFrameDecoder解码器 它支持自动的TCP粘包和半包处理
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder{

    private NettyMarshallingDecoder nettyMarshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.nettyMarshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx,in);
        if(byteBuf == null){
            return null;
        }
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if(size > 0){
            Map<String,Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte [] keyArray  = null;
            String key = null;
            for(int i = 0; i< size; i++){
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray,"UTF-8");
                attch.put(key,nettyMarshallingDecoder.decode(ctx,in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }
        if(in.readableBytes() > 4){
            nettyMessage.setBody(nettyMarshallingDecoder.decode(ctx,in));
        }

        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
