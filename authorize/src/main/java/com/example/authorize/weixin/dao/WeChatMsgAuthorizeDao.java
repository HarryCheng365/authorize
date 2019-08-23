package com.example.authorize.weixin.dao;

import com.example.authorize.weixin.entity.AuthorizeMsg;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeChatMsgAuthorizeDao {
    void insertRecord(AuthorizeMsg authorizeMsg);
    void updateRecord(AuthorizeMsg authorizeMsg);
    @Select("SELECT componentVerifyTicket FROM wechat_authorizer_msg where infoType='component_verify_ticket' and appId=#{appId} order by createTime DESC limit 0,1 ")
    String getLastestComponentVerifyTickets(@Param("appId") String appId);

    @Select("SELECT * FROM wechat_authorizer_msg where infoType =#{infoType} limit #{curIndex} ,#{pageSize}")
    List<AuthorizeMsg> getAuthorizeMsgByInfoType(@Param("infoType") String infoType,@Param("curIndex") int index,@Param("pageSize") int pageSize);

    /**
     * @param msgAuthorizeQueryDTO
     * @return
     */
    List<AuthorizeMsg> selectAuthorizeMsg(MsgAuthorizeQueryDTO msgAuthorizeQueryDTO);

    /**
     *
     * @param msgAuthorizeQueryDTO
     * @return
     */
    List<String> selectRecordId(MsgAuthorizeQueryDTO msgAuthorizeQueryDTO);

}
