package org.tony.netty.agreement.message.utils;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.marshalling.server
 */
public final class MarshallingCodeCFactory {

    /***
     * 创建Jboss的解码器
     * @return
     */
    public static NettyMarshallingDecoder buildMarshallingDecoder(){
        //获得marshallerFactory的实例，serial参数表示对java序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,configuration);
        NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider,1024);
        return decoder;
    }

    /***
     * 创建JBoss的加码器
     */
    public static NettyMarshallingEncoder buildMarshallingEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory,configuration);
        NettyMarshallingEncoder marshallingEncoder = new NettyMarshallingEncoder(provider);
        return marshallingEncoder;
    }
}
