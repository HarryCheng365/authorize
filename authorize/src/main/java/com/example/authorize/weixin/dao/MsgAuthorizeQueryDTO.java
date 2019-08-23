package com.example.authorize.weixin.dao;

public class MsgAuthorizeQueryDTO {
    private String appId;
    private String authorizerAppId;
    private String infoType;
    private Long createTimeStart;
    private Long createTimeEnd;

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public String getAppId() {
        return appId;
    }

    public String getInfoType() {
        return infoType;
    }

    public Long getCreateTimeEnd() {
        return createTimeEnd;
    }

    public Long getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeEnd(Long createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public void setCreateTimeStart(Long createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }


}
