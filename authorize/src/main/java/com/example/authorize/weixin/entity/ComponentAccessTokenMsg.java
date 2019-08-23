package com.example.authorize.weixin.entity;

public class ComponentAccessTokenMsg {
    private int id;
    private String componentAppId;
    private String componentAccessToken;
    private int expiresIn;
    private Long createTime;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getComponentAccessToken() {
        return componentAccessToken;
    }

    public String getComponentAppId() {
        return componentAppId;
    }

    public void setComponentAccessToken(String componentAccessToken) {
        this.componentAccessToken = componentAccessToken;
    }

    public void setComponentAppId(String componentAppId) {
        this.componentAppId = componentAppId;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
