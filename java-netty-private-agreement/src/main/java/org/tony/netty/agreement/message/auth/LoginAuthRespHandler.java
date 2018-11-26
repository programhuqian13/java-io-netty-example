package org.tony.netty.agreement.message.auth;

import io.netty.channel.ChannelHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message.auth
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter{

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();
}
