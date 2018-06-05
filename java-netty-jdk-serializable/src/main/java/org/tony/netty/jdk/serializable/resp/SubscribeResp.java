package org.tony.netty.jdk.serializable.resp;

import java.io.Serializable;

/**
 * @Description 描述
 * @Version 1.0
 * @Author xuanyi@baofu.com
 * @Date 2018/5/17
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.jdk.serializable.resp
 */
public class SubscribeResp implements Serializable {

    private Integer subReqID;

    private Integer respCode;

    private String desc;

    public Integer getSubReqID() {
        return subReqID;
    }

    public void setSubReqID(Integer subReqID) {
        this.subReqID = subReqID;
    }

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SubscribeResp{" +
                "subReqID=" + subReqID +
                ", respCode=" + respCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
