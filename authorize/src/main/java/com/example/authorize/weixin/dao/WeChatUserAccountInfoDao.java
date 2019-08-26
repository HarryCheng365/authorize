package com.example.authorize.weixin.dao;

import com.example.authorize.weixin.entity.AuthorizeAccountInfoMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatUserAccountInfoDao {

    void insertRecord(AuthorizeAccountInfoMsg authorizeAccountInfoMsg);
    void updateRecord(AuthorizeAccountInfoMsg authorizeAccountInfoMsg);

    @Select("SELECT * FROM wechat_user_account_info where authorizerAppId =#{authorizerAppId}")
    String getAccountInfoById(@Param("authorizerAppId") String authorizerAppId);




}
