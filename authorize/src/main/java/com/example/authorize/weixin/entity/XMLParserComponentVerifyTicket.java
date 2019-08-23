package com.example.authorize.weixin.entity;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLParserComponentVerifyTicket {

    @XmlElement(name= "AppId")
    private String appId;

    @XmlElement(name= "CreateTime")
    private String createTime;

    @XmlElement(name= "InfoType")
    private String infoType;

    @XmlElement(name= "ComponentVerifyTicket")
    private String componentVerifyTicket;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfoType() {
        return infoType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getAppId() {
        return appId;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getComponentVerifyTicket() {
        return componentVerifyTicket;
    }

    public void setComponentVerifyTicket(String componentVerifyTicket) {
        this.componentVerifyTicket = componentVerifyTicket;
    }
}
