package com.example.authorize.weixin.dao;

import com.example.authorize.weixin.entity.AuthorizeAccessTokenMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatUserAccessTokenDao {
    void insertRecord(AuthorizeAccessTokenMsg authorizeAccessTokenMsg);
    void updateRecord(AuthorizeAccessTokenMsg authorizeAccessTokenMsg);

    @Select("SELECT authorizerRefreshToken FROM wechat_user_access_tokens where authorizerAppId = #{authorizerAppId}")
    String getRefreshToken(@Param("authorizerAppId") String authorizerAppId);

    @Select("SELECT * FROM wechat_user_access_tokens where authorizerAppId =#{authorizerAppId")
    AuthorizeAccessTokenMsg getAuthorizeTokenMsg(@Param("authorizerAppId") String authorizerAppId);



}
