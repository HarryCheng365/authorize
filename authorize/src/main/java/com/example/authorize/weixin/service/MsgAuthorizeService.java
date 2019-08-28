package com.example.authorize.weixin.service;


import com.example.authorize.weixin.aes.WXBizMsgCrypt;
import com.example.authorize.weixin.api.ComponentAPI;
import com.example.authorize.weixin.client.BaseResult;
import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.dao.WeChatMsgAuthorizeDao;
import com.example.authorize.weixin.dao.WeChatUserAccessTokenDao;
import com.example.authorize.weixin.dao.WeChatUserAccountInfoDao;
import com.example.authorize.weixin.entity.*;
import com.example.authorize.weixin.support.RedisService;
import com.example.authorize.weixin.util.XMLConverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Haoyu
 * @date 2019/8/21
 */
@Service
public class MsgAuthorizeService {
    private Logger logger = LoggerFactory.getLogger(MsgAuthorizeService.class);

    @Autowired
    WeChatMsgAuthorizeDao weChatMsgAuthorizeDao;

    @Autowired
    WeChatUserAccessTokenDao weChatUserAccessTokenDao;

    @Autowired
    WeChatUserAccountInfoDao weChatUserAccountInfoDao;

    @Autowired
    RedisService redisService;

    public String parseComponentTicket(String body) {
        try{
        WXBizMsgCrypt wxBizMsgCrypt = WXBizMsgCrypt.getWxBizMsgCrypt();
        String decryptMsg = wxBizMsgCrypt.decryptMsg(body);
        AuthorizeMsg componentVerifyTickets = XMLConverUtil.convertToObject(AuthorizeMsg.class, decryptMsg);
        saveComponentTicket(componentVerifyTickets);
        }catch (Exception e){
            logger.error("ComponentTickets XML转换失败"+e.getMessage());
        }
        return "success";
    }

    public String getLastestVerifyTickets(String appId) {
        String result =null;
        try{
        result= weChatMsgAuthorizeDao.getLastestComponentVerifyTickets(appId);
        }catch (Exception e){
            logger.error("获取最新ComponentVerifyTickets失败"+e.getMessage());
        }
        return result;

    }

    public void saveComponentTicket(AuthorizeMsg authorizeMsg){
        try {
            weChatMsgAuthorizeDao.insertRecord(authorizeMsg);
            logger.info(authorizeMsg.getAppId()+authorizeMsg.getComponentVerifyTicket());
        }catch (Exception e){
            logger.error("AuthorizeMsg 存入信息失败"+e.getMessage());
        }
    }

    public String getComponentAccessToken()throws Exception{

        return redisService.get(AuthorizeConsts.appId + AuthorizeConsts.componentAccessToken);
    }


    public String getPreAuthCode(String appId)throws Exception{
        String result=null;
        try {
            if(redisService.exists(appId+AuthorizeConsts.preAuthCode)){
             result=redisService.get(appId+AuthorizeConsts.preAuthCode);
            }
            else{
                for(int i=0;i<10;i++) {
                    String componentAccessToken = getComponentAccessToken();
                    PreAuthCode preAuthCode = ComponentAPI.getApiPreauthcode(componentAccessToken, AuthorizeConsts.appId);
                    if (preAuthCode.isSuccess()) {
                        result = preAuthCode.getPreAuthCode();
                        break;

                    }else{
                        Thread.sleep(1000);
                    }
                }
            }

        }catch (Exception e){
            logger.error("PRE_AUTH_CODE 获取失败",e);
            throw new Exception("PRE_AUTH_CODE 获取失败",e);
        }
        return result;

    }
    public void saveUserAccessToken(String tid,String auth_code,int expires_in){
        try{

            String componentAccessToken = redisService.get(AuthorizeConsts.appId+AuthorizeConsts.componentAccessToken);
            ApiQueryAuthResult apiQueryAuthResult=null;
            for(int i=0;i<10;i++){
                apiQueryAuthResult = ComponentAPI.api_query_auth(componentAccessToken, AuthorizeConsts.appId,auth_code);
                if(apiQueryAuthResult.isSuccess()) {
                    break;
                }else{
                    logger.info("AUTH_ACCESS_TOKEN http 请求失败");
                }
            }
            ApiQueryAuthResult.Authorization_info authorization_info = apiQueryAuthResult.getAuthorization_info();
            AuthorizeAccessTokenMsg authorizeAccessTokenMsg =new AuthorizeAccessTokenMsg(authorization_info.getAuthorizer_appid(),
                    authorization_info.getAuthorizer_access_token(),authorization_info.getAuthorizer_refresh_token(),authorization_info.getExpires_in());
            if(weChatUserAccessTokenDao.getAuthorizeTokenMsg(authorization_info.getAuthorizer_appid())==null){
                weChatUserAccessTokenDao.insertRecord(authorizeAccessTokenMsg);
                logger.info("新增AUTH_ACCESS_TOKEN "+authorizeAccessTokenMsg.getAuthorizerAppId());
                ApiGetAuthorizerInfoResult apiGetAuthorizerInfoResult =ComponentAPI.api_get_authorizer_info(componentAccessToken,
                        AuthorizeConsts.appId,authorizeAccessTokenMsg.getAuthorizerAppId());
            }else{
                weChatUserAccessTokenDao.updateRecord(authorizeAccessTokenMsg);
                logger.info("更新AUTH_ACCESS_TOKEN "+authorizeAccessTokenMsg.getAuthorizerAppId());
            }

            saveUserAccountInfo(authorization_info.getAuthorizer_appid(),tid);

        }catch (Exception e){
            logger.error("AUTH_ACCESS_TOKEN error",e);

        }

    }

    public void saveUserAccountInfo(String authorizer_appid,String tid){
        try {
            logger.info("USER_ACCOUNT_INFO 开始拉取用户信息");
            String componentAccessToken = redisService.get(AuthorizeConsts.appId + AuthorizeConsts.componentAccessToken);
            ApiGetAuthorizerInfoResult apiGetAuthorizerInfoResult = null;
            for (int i = 0; i < 10; i++) {
                apiGetAuthorizerInfoResult = ComponentAPI.api_get_authorizer_info(componentAccessToken, AuthorizeConsts.appId, authorizer_appid);
                if (apiGetAuthorizerInfoResult.isSuccess()) {
                    break;
                }
            }
            ApiGetAuthorizerInfoResult.Authorization_info authorizationInfo = apiGetAuthorizerInfoResult.getAuthorization_info();
            ApiGetAuthorizerInfoResult.Authorizer_info authorizerInfo = apiGetAuthorizerInfoResult.getAuthorizer_info();
            AuthorizeAccountInfoMsg authorizeAccountInfoMsg = new AuthorizeAccountInfoMsg(tid,authorizationInfo.getAuthorizer_appid(), authorizerInfo.getUser_name(), authorizerInfo.getNick_name(),
                    authorizerInfo.getPrincipal_name(), authorizerInfo.getHead_img(), authorizerInfo.getService_type_info().getId(), authorizerInfo.getVerify_type_info().getId(), authorizerInfo.getAlias(),
                    authorizerInfo.getBusiness_info().toString(), authorizerInfo.getQrcode_url(),
                    authorizationInfo.getFunc_info().toString());
            weChatUserAccountInfoDao.insertRecord(authorizeAccountInfoMsg);
            for(int i=0;i<10;i++){
                BaseResult baseResult=ComponentAPI.authorizeFinish(AuthorizeConsts.appId,authorizer_appid,tid);
                if(baseResult.isSuccess()){
                    break;
                }else{
                    logger.error("授权信息送回网易号失败:{},{}",baseResult.getErrcode(),baseResult.getErrmsg());
                    Thread.sleep(5000);
                }
            }
        }catch (Exception e){
            logger.error("AUTHORIZER_ACCOUNT_INFO error",e);
        }
    }





}
