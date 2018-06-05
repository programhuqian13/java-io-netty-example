import org.tony.netty.serialzable.entity.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName PACKAGE_NAME
 */
public class PerformTestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserId(100).buildUserName("Welcome to netty.");
        int loop = 1000000;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        Long startTime = System.currentTimeMillis();
        for(int i= 0 ;i < loop;i++){
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(userInfo);
            out.flush();
            out.close();
            byte [] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("-----------------------the jdk serialzable cost time is : " + (endTime - startTime) + " ms");
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for(int i = 0;i < loop;i++){
            byte [] b = userInfo.codeC(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("---------------the byte array serialzable cost time is : " + (endTime - startTime) + " ms");
    }

}
