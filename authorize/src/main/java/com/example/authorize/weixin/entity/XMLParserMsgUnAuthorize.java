package com.example.authorize.weixin.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLParserMsgUnAuthorize {

    @XmlElement(name= "AppId")
    private String appId;

    @XmlElement(name= "CreateTime")
    private String createTime;

    @XmlElement(name= "InfoType")
    private String infoType;

    @XmlElement(name= "AuthorizerAppid")
    private String authorizeAppid;


    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAppId() {
        return appId;
    }

    public String getAuthorizeAppid() {
        return authorizeAppid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAuthorizeAppid(String authorizeAppid) {
        this.authorizeAppid = authorizeAppid;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }
}
