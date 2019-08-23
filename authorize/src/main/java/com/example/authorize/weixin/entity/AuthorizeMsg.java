package com.example.authorize.weixin.entity;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizeMsg {

    private int id;
    @XmlElement(name= "AppId")
    private String appId;

    @XmlElement(name=  "CreateTime")
    private Long createTime;

    @XmlElement(name= "InfoType")
    private String infoType;

    @XmlElement(name= "AuthorizerAppid")
    private String authorizerAppId;

    @XmlElement(name= "AuthorizationCode")
    private String authorizationCode;

    @XmlElement(name = "ComponentVerifyTicket")
    private String componentVerifyTicket;

    @XmlElement(name= "AuthorizationCodeExpiredTime")
    private String authorizationCodeExpiredTime;

    @XmlElement(name= "PreAuthCode")
    private String preAuthCode;
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getAuthorizationCodeExpiredTime() {
        return authorizationCodeExpiredTime;
    }

    public void setAuthorizationCodeExpiredTime(
            String authorizationCodeExpiredTime) {
        this.authorizationCodeExpiredTime = authorizationCodeExpiredTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    /**
     * component_verify_ticket 推送component_verify_ticket协议
     * unauthorized 取消授权
     * updateauthorized 更新授权
     * authorized 授权成功通知
     *
     * @return infoType
     */
    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getAuthorizerAppid() {
        return authorizerAppId;
    }

    public void setAuthorizerAppid(String authorizerAppid) {
        this.authorizerAppId = authorizerAppid;
    }

    public void setComponentVerifyTicket(String componentVerifyTicket) {
        this.componentVerifyTicket = componentVerifyTicket;
    }

    public String getComponentVerifyTicket() {
        return componentVerifyTicket;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }

    public String getPreAuthCode() {
        return preAuthCode;
    }

    public void setPreAuthCode(String preAuthCode) {
        this.preAuthCode = preAuthCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}



