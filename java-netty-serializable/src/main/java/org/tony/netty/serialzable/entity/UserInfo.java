package org.tony.netty.serialzable.entity;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @Description 使用jdk自带的序列化
 *      使用序列化的作用为1.进行网络传输  2.进行对象持久化
 *      JDK自带的序列化有两个缺点：
 *          1.不能跨语言，使用java序列化对象不能使用其他语言进行反序列化
 *          2.序列化后的码流太大
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.serialzable.entity
 */
public class UserInfo implements Serializable{

    private Integer userId;

    private String userName;

    public UserInfo buildUserId(Integer userId){
        this.userId = userId;
        return this;
    }

    public UserInfo buildUserName(String userName){
        this.userName = userName;
        return this;
    }

    public final String getUserName(){
        return this.userName;
    }

    public final void setUserName(final String userName){
        this.userName = userName;
    }

    public final Integer getUserId(){
        return this.userId;
    }

    public final void setUserId(final Integer userId){
        this.userId = userId;
    }

    public byte [] codeC(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte [] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userId);
        buffer.flip();
        value = null;
        byte [] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public byte [] codeC(ByteBuffer byteBuffer){
        byteBuffer.clear();
        byte [] value = this.userName.getBytes();
        byteBuffer.putInt(value.length);
        byteBuffer.put(value);
        byteBuffer.putInt(this.userId);
        byteBuffer.flip();
        value = null;
        byte [] result = new byte[byteBuffer.remaining()];
        byteBuffer.get(result);
        return result;
    }
}
