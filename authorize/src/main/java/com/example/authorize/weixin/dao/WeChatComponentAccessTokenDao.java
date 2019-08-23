package com.example.authorize.weixin.dao;

import com.example.authorize.weixin.entity.ComponentAccessTokenMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatComponentAccessTokenDao {

    void insertRecord(ComponentAccessTokenMsg componentAccessTokenMsg);
    void updateRecord(ComponentAccessTokenMsg componentAccessTokenMsg);


    @Select("SELECT componentAccessToken FROM wechat_component_access_tokens where componentAppId =#{componentAppId} order by createTime limit 1")
    String getLatestComponentAccessToken(@Param("componentAppId") String componentAppId);

}
