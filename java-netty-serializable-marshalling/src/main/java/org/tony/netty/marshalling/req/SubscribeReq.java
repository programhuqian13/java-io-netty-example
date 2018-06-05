package org.tony.netty.marshalling.req;

import java.io.Serializable;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/18
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.marshalling.req
 */
public class SubscribeReq implements Serializable {

    private Integer subReqID;

    private String userName;

    private String address;

    private String productName;

    public SubscribeReq buildUserName(String userName){
        this.userName = userName;
        return this;
    }

    public SubscribeReq buildSubscribeID(Integer subReqID){
        this.subReqID = subReqID;
        return this;
    }

    public Integer getSubReqID() {
        return subReqID;
    }

    public void setSubReqID(Integer subReqID) {
        this.subReqID = subReqID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "SubscribeReq{" +
                "subReqID=" + subReqID +
                ", userName='" + userName + '\'' +
                ", address='" + address + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
