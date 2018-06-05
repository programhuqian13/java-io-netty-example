package org.tony.netty.agreement.message;

/**
 * @Description netty消息定义
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message
 */
public final class NettyMessage  {

    private Header header;  //消息头

    private Object body;  //消息体

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Netty Message [header=" + header + "]";
    }
}
