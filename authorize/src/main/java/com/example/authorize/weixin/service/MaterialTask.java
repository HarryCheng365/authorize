package com.example.authorize.weixin.service;

import com.alibaba.fastjson.JSONObject;
import com.example.authorize.controller.MaterialController;
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
import com.example.authorize.weixin.entity.material.MaterialcountResult;
import com.example.authorize.weixin.support.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Haoyu
 * @date 2019/8/27
 */
@Component
public class MaterialTask {
    private Logger logger = LoggerFactory.getLogger(MaterialTask.class);

    private  ScheduledExecutorService scheduledExecutorService;

    private  int poolSize = 5;

    private  boolean daemon = Boolean.TRUE;

    @Autowired
    WeChatUserAccountInfoDao weChatUserAccountInfoDao;

    @Autowired
    RedisService redisService;

    @Autowired
    WeChatUserAccessTokenDao weChatUserAccessTokenDao;

    @Autowired
    MaterialService materialService;


    public void initMaterialTask(){
        initMaterialTask(0,1800);
    }

    private void initMaterialTask(int initialDelay,int delay){

        if(scheduledExecutorService == null){
            initScheduledExecutorService();
        }
        //立即执行一次
        if(initialDelay == 0){
            refreshMaterialTask(true);
        }
        ScheduledFuture<?> scheduledFuture =  scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                refreshMaterialTask(false);
            }
        },initialDelay == 0 ? delay : initialDelay,delay,TimeUnit.SECONDS);
        logger.info("MaterialTask task start");

    }
    private void refreshMaterialTask(boolean isInitialize){
        ThreadPoolExecutor threadPoolExecutor =new ThreadPoolExecutor(10, 10, 600, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread =new Thread(r);
                thread.setName("MATERIAL_SERVICE_FIND_MATERIAL_LIST");
                return thread;
            }
        });

        int totalNum=weChatUserAccountInfoDao.countAuthorizerAppIds();
        int page =totalNum%MaterialConsts.ACCOUNT_PAGE_SIZE==0?totalNum/MaterialConsts.ACCOUNT_PAGE_SIZE:totalNum/MaterialConsts.ACCOUNT_PAGE_SIZE+1;
        for(int i=0;i<page;i++){
            List<String> authorizerIds=findAuthorizerAppId(i,MaterialConsts.ACCOUNT_PAGE_SIZE);
            for(String appid:authorizerIds){
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        findMaterialList(appid,materialService.getUserAccessToken(appid),isInitialize);
                    }
                });
            }
        }

    }
    private void findMaterialList(String authorizer_appid,String access_token,boolean isInitialize){
        try {

            int newsCount=getNewsCount(access_token);
            int page=newsCount%MaterialConsts.MATERIAL_PAGE_SIZE==0?newsCount/MaterialConsts.MATERIAL_PAGE_SIZE:newsCount/MaterialConsts.MATERIAL_PAGE_SIZE+1;
            page=isInitialize?page:1;
            MaterialBatchgetResult materialBatchgetResult=null;
            List<MaterialBatchgetResultItem> materialBatchgetResultItems =new ArrayList<>();

            for(int i=0;i<page;i++){
                for(int j=0;j<10;j++){
                    materialBatchgetResult = MaterialAPI.batchget_material(access_token,"news",i, MaterialConsts.MATERIAL_PAGE_SIZE);
                    if(materialBatchgetResult.isSuccess()){
                        materialBatchgetResultItems=materialBatchgetResult.getItem();
                        break;
                    }else{
                        Thread.sleep(5000);
                    }
                }
                for(MaterialBatchgetResultItem resultItem:materialBatchgetResultItems){
                    String media_id=resultItem.getMedia_id();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("authorizer_appid",authorizer_appid);
                    jsonObject.put("media_id",media_id);
                    redisService.lpush(MaterialConsts.MATERIAL_QUEUE,jsonObject.toJSONString());
                    logger.info("news 推送成功authorizer_appid:{},media_id:{}",authorizer_appid,media_id);
                }
            }

        }catch (Exception e){
            logger.error("FIND_MATERIAL_TASK failed",e);

        }

    }


    private List<String> findAuthorizerAppId(int curIndex,int pageSize){
        List<String>  list=new ArrayList<>();
        try{
            list=weChatUserAccountInfoDao.getAuthorizerAppIds(curIndex,pageSize);
        }catch (Exception e){

        }
        return list;
    }

    private int getNewsCount(String access_token){
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
            logger.error("GET_NEWS_ACCOUNT count新闻总数failed");

        }
        return newsCount;

    }


    private  void initScheduledExecutorService(){
        logger.info("daemon:{},poolSize:{}",daemon,poolSize);
        scheduledExecutorService =  Executors.newScheduledThreadPool(poolSize,new ThreadFactory() {

            @Override
            public Thread newThread(Runnable arg0) {
                Thread thread = Executors.defaultThreadFactory().newThread(arg0);
                //设置守护线程
                thread.setDaemon(daemon);
                return thread;
            }
        });
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
