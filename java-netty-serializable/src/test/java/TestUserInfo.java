import org.tony.netty.serialzable.entity.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName PACKAGE_NAME
 */
public class TestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserId(100).buildUserName("Welcome to netty.");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(userInfo);
        out.flush();
        out.close();
        byte [] b = bos.toByteArray();
        System.out.println("------------the jdk serialzable length is : " + b.length);
        bos.close();
        System.out.println("--------------------------------------------------");
        System.out.println("the byte array serialzable length is : " + userInfo.codeC().length);
    }

}
