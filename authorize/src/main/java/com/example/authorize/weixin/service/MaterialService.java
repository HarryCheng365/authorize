package com.example.authorize.weixin.service;

import com.example.authorize.weixin.api.ComponentAPI;
import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.dao.WeChatUserAccessTokenDao;
import com.example.authorize.weixin.entity.AuthorizeAccessTokenMsg;
import com.example.authorize.weixin.entity.AuthorizerAccessToken;
import com.example.authorize.weixin.support.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Haoyu
 * @date 2019/08/27
 */
@Service
public class MaterialService {


    private Logger logger = LoggerFactory.getLogger(MaterialService.class);

    @Autowired
    RedisService redisService;

    @Autowired
    WeChatUserAccessTokenDao weChatUserAccessTokenDao;
    /**
     * 根据authorizer_appid去取相应的access_token token采用lazy的方式 不会自动刷新，如果发现redis缓存里没有，那么就去数据库中取出
     * refreshToken 进行刷新
     * @param authorizer_appid
     * @return
     */
    public String getUserAccessToken(String authorizer_appid){
        String userAccessToken=null;
        try {
            userAccessToken= redisService.get(AuthorizeConsts.APP_ID + authorizer_appid + AuthorizeConsts.USER_ACCESS_TOKEN);
            String componentAccessToken = redisService.get(AuthorizeConsts.APP_ID + AuthorizeConsts.COMPONENT_ACCESS_TOKEN);
            if (userAccessToken == null) {
                AuthorizeAccessTokenMsg authorizeAccessTokenMsg = weChatUserAccessTokenDao.getAuthorizeTokenMsg(authorizer_appid);
                String refreshToken = authorizeAccessTokenMsg.getAuthorizerRefreshToken();
                for (int i = 0; i < 10; i++) {
                    AuthorizerAccessToken authorizerAccessToken = ComponentAPI.api_authorizer_token(componentAccessToken, AuthorizeConsts.APP_ID, authorizer_appid, refreshToken);
                    if (authorizerAccessToken.isSuccess()) {
                        authorizeAccessTokenMsg.setAuthorizerAccessToken(authorizerAccessToken.getAuthorizerAccessToken());
                        authorizeAccessTokenMsg.setAuthorizerRefreshToken(authorizerAccessToken.getAuthorizerRefreshToken());
                        weChatUserAccessTokenDao.updateRecord(authorizeAccessTokenMsg);
                        redisService.set(AuthorizeConsts.APP_ID + authorizer_appid + AuthorizeConsts.USER_ACCESS_TOKEN,authorizerAccessToken.getAuthorizerAccessToken(),118*60);
                        userAccessToken=authorizerAccessToken.getAuthorizerAccessToken();
                        break;
                    }
                }
            }
        }catch (Exception e){
            logger.error("USER_ACCESS_TOKEN error",e);
        }
        return userAccessToken;

    }
}

