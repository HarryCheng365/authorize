package com.example.authorize.weixin.dao;

import com.example.authorize.weixin.entity.AuthorizeAccountInfoMsg;
import com.example.authorize.weixin.entity.AuthorizerAccessToken;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeChatUserAccountInfoDao {

    void insertRecord(AuthorizeAccountInfoMsg authorizeAccountInfoMsg);
    void updateRecord(AuthorizeAccountInfoMsg authorizeAccountInfoMsg);

    @Select("SELECT * FROM wechat_user_account_info where authorizerAppId =#{authorizerAppId}")
    AuthorizeAccountInfoMsg getAccountInfoById(@Param("authorizerAppId") String authorizerAppId);

    @Select("SELECT * FROM wechat_user_account_info where tid=#{tid}")
    AuthorizeAccountInfoMsg getAccountInfoByTid(@Param("tid") String tid);


    @Select("SELECT authorizerAppId FROM wechat_user_account_info limit #{curIndex} ,#{pageSize}")
    List<String> getAuthorizerAppIds(@Param("curIndex") int curIndex,@Param("pageSize") int pageSize);

    @Select("SELECT count(*) FROM wechat_user_account_info")
    int countAuthorizerAppIds();





}
