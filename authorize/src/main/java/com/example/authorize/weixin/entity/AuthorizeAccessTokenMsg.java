package com.example.authorize.weixin.entity;

import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.util.DateUtils;

import java.util.Date;

public class AuthorizeAccessTokenMsg {

    private String authorizerAppId;

    private String authorizerAccessToken;

    private Integer expiresIn;

    private String authorizerRefreshToken;

    private Long createTime;

    public AuthorizeAccessTokenMsg(String authorizerAppId, String authorizerAccessToken, String authorizerRefreshToken, Integer expiresIn){
        this.authorizerAppId=authorizerAppId;
        this.authorizerAccessToken=authorizerAccessToken;
        this.authorizerRefreshToken=authorizerRefreshToken;
        this.expiresIn=expiresIn;
        this.createTime= System.currentTimeMillis()/ AuthorizeConsts.ONE_SECOND;

    }

    public String getAuthorizerAccessToken() {
        return authorizerAccessToken;
    }

    public void setAuthorizerAccessToken(String authorizerAccessToken) {
        this.authorizerAccessToken=authorizerAccessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn= expiresIn;
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }
}
