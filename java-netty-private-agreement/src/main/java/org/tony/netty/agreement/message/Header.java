package org.tony.netty.agreement.message;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 消息头
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/24
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.agreement.message
 */
public final class Header {

    private int crcCode = 0xabef0101;

    private Integer length; //消息长度

    private Long sessionId;  //会话ID

    private Byte type;  //消息类型

    private Byte priority;  //消息优先级

    private Map<String,Object> attachment = new HashMap<>();  //附件

    public final int getCrcCode() {
        return crcCode;
    }

    public final void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public final Integer getLength() {
        return length;
    }

    public final void setLength(Integer length) {
        this.length = length;
    }

    public final Long getSessionId() {
        return sessionId;
    }

    public final void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public final Byte getType() {
        return type;
    }

    public final void setType(Byte type) {
        this.type = type;
    }

    public final Byte getPriority() {
        return priority;
    }

    public final void setPriority(Byte priority) {
        this.priority = priority;
    }

    public final Map<String, Object> getAttachment() {
        return attachment;
    }

    public final void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
