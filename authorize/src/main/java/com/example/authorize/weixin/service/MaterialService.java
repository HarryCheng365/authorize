package com.example.authorize.weixin.service;

import com.example.authorize.weixin.api.ComponentAPI;
import com.example.authorize.weixin.api.MaterialAPI;
import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.consts.MaterialConsts;
import com.example.authorize.weixin.dao.WeChatUserAccessTokenDao;
import com.example.authorize.weixin.dao.WeChatUserAccountInfoDao;
import com.example.authorize.weixin.entity.AuthorizeAccessTokenMsg;
import com.example.authorize.weixin.entity.AuthorizerAccessToken;
import com.example.authorize.weixin.entity.material.MaterialBatchgetResult;
import com.example.authorize.weixin.entity.material.MaterialBatchgetResultItem;
import com.example.authorize.weixin.entity.material.MaterialGetResult;
import com.example.authorize.weixin.entity.material.MaterialcountResult;
import com.example.authorize.weixin.support.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class MaterialService {
    private Logger logger = LoggerFactory.getLogger(MaterialService.class);

    @Autowired
    WeChatUserAccountInfoDao weChatUserAccountInfoDao;

    @Autowired
    RedisService redisService;

    @Autowired
    WeChatUserAccessTokenDao weChatUserAccessTokenDao;


    public List<String> findAuthorizerAppId(int curIndex,int pageSize){
        List<String>  list=new ArrayList<>();
        try{
            list=weChatUserAccountInfoDao.getAuthorizerAppIds(curIndex,pageSize);
        }catch (Exception e){

        }
        return list;
    }

    public int getNewsCount(String access_token){
        int newsCount = 0;
        try {
            MaterialcountResult materialcountResult = null;
            for (int i = 0; i < 10; i++) {
                materialcountResult = MaterialAPI.get_materialcount(access_token);
                if (materialcountResult.isSuccess()) {
                    newsCount = materialcountResult.getNews_count();
                    break;
                } else {
                    Thread.sleep(5000);
                }

            }

        }catch (Exception e){

        }
        return newsCount;

    }
    public void findMaterialList(String authorizer_appid,String access_token,boolean isInitialize){
        try {

            int newsCount=getNewsCount(access_token);
            int page=newsCount%MaterialConsts.MATERIAL_PAGE_SIZE==0?newsCount/MaterialConsts.MATERIAL_PAGE_SIZE:newsCount/MaterialConsts.MATERIAL_PAGE_SIZE+1;
            page=isInitialize?page:1;
            MaterialBatchgetResult materialBatchgetResult=null;
            List<MaterialBatchgetResultItem> materialBatchgetResultItems =new ArrayList<>();
            ThreadPoolExecutor threadPoolExecutor =new ThreadPoolExecutor(10, 10, 600, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread =new Thread(r);
                    thread.setName("MATERIAL_SERVICE_FIND_MATERIAL_LIST");
                    return thread;
                }
            });

            for(int i=0;i<page;i++){
                for(int j=0;j<10;j++){
                    materialBatchgetResult = MaterialAPI.batchget_material(access_token,"news",0, MaterialConsts.MATERIAL_PAGE_SIZE);
                    if(materialBatchgetResult.isSuccess()){
                        materialBatchgetResultItems=materialBatchgetResult.getItem();
                        break;
                    }else{
                        Thread.sleep(5000);
                    }
                }
                for(MaterialBatchgetResultItem resultItem:materialBatchgetResultItems){
                    String media_id=resultItem.getMedia_id();
                    redisService.lpush(MaterialConsts.MATERIAL_QUEUE,media_id);
                }
            }






        }catch (Exception e){

        }



    }



    /**
     * 根据authorizer_appid去取相应的access_token token采用lazy的方式 不会自动刷新，如果发现redis缓存里没有，那么就去数据库中取出
     * refreshToken 进行刷新
     * @param authorizer_appid
     * @return
     */
    public String getUserAccessToken(String authorizer_appid){
        String userAccessToken=null;
        try {
            userAccessToken= redisService.get(AuthorizeConsts.appId + authorizer_appid + AuthorizeConsts.userAccessToken);
            String componentAccessToken = redisService.get(AuthorizeConsts.appId + AuthorizeConsts.componentAccessToken);
            if (userAccessToken == null) {
                AuthorizeAccessTokenMsg authorizeAccessTokenMsg = weChatUserAccessTokenDao.getAuthorizeTokenMsg(authorizer_appid);
                String refreshToken = authorizeAccessTokenMsg.getAuthorizerRefreshToken();
                for (int i = 0; i < 10; i++) {
                    AuthorizerAccessToken authorizerAccessToken = ComponentAPI.api_authorizer_token(componentAccessToken, AuthorizeConsts.appId, authorizer_appid, refreshToken);
                    if (authorizerAccessToken.isSuccess()) {
                        authorizeAccessTokenMsg.setAuthorizerAccessToken(authorizerAccessToken.getAuthorizerAccessToken());
                        authorizeAccessTokenMsg.setAuthorizerRefreshToken(authorizerAccessToken.getAuthorizerRefreshToken());
                        weChatUserAccessTokenDao.updateRecord(authorizeAccessTokenMsg);
                        redisService.set(AuthorizeConsts.appId + authorizer_appid + AuthorizeConsts.userAccessToken,authorizerAccessToken.getAuthorizerAccessToken(),118*60);
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



//    private class MaterialRun implements Runnable{
//        private String mediaId=null;
//        private String authorizer_appid=null;
//        public MaterialRun(String mediaId,String authorizer_appid){
//            this.mediaId=mediaId;
//            this.authorizer_appid=authorizer_appid;
//
//        }
//        @Override
//        public void run(){
//            String accessToken =getUserAccessToken(authorizer_appid);
//            MaterialGetResult materialGetResul= MaterialAPI.get_material(accessToken,mediaId);
//
//        }
//    }
}
