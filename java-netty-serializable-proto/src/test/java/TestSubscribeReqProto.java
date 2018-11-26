import com.google.protobuf.InvalidProtocolBufferException;
import org.tony.netty.google.protobuf.req.SubscribeReqProto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 描述
 * @Version 1.0
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName PACKAGE_NAME
 */
public class TestSubscribeReqProto {

    private static byte [] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();  //可将SubscribeReq编码为byte数组
    }

    private static SubscribeReqProto.SubscribeReq decode(byte [] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);  //将二进制编码转化为原始对象
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("tony");
        builder.setProductName("Netty book");
        builder.setAddress("ShangHai PuDongXinqu");
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode : " + req.toString());
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode : " + req.toString());
        System.out.println("Assert equal : " + req2.equals(req));
    }

}
