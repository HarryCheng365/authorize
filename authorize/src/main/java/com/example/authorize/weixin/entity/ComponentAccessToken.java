package com.example.authorize.weixin.entity;


import com.example.authorize.weixin.client.BaseResult;

public class ComponentAccessToken extends BaseResult {

    private String componentAccessToken;

    private int expiresIn;

    public String getComponentAccessToken() {
        return componentAccessToken;
    }

    public void setComponentAccessToken(String componentAccessToken) {
        this.componentAccessToken = componentAccessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }


}
