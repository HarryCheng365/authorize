package com.example.authorize.weixin.entity;

import com.example.authorize.weixin.client.BaseResult;

public class PreAuthCode extends BaseResult {

    private String preAuthCode;

    private int expiresIn;

    public String getPreAuthCode() {
        return preAuthCode;
    }
    public void setPreAuthCode(String preAuthCode) {
        this.preAuthCode = preAuthCode;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }


}
