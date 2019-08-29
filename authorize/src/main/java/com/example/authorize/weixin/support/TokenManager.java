package com.example.authorize.weixin.support;

import com.example.authorize.weixin.api.ComponentAPI;
import com.example.authorize.weixin.api.TokenAPI;
import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.dao.WeChatComponentAccessTokenDao;
import com.example.authorize.weixin.dao.WeChatMsgAuthorizeDao;
import com.example.authorize.weixin.entity.ComponentAccessToken;
import com.example.authorize.weixin.entity.ComponentAccessTokenMsg;
import com.example.authorize.weixin.entity.PreAuthCode;
import com.example.authorize.weixin.entity.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.concurrent.*;

/**
 * TokenManager token 自动刷新
 * @author Haoyu
 *
 */
@Component
public class TokenManager {

	private  final Logger logger = LoggerFactory.getLogger(TokenManager.class);
	
	private  ScheduledExecutorService scheduledExecutorService;

	private  Map<String,String> tokenMap = new ConcurrentHashMap<String,String>();

	private  Map<String,ScheduledFuture<?>> futureMap = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	private  int poolSize = 5;
	
	private  boolean daemon = Boolean.TRUE;

	@Autowired
	WeChatMsgAuthorizeDao weChatMsgAuthorizeDao;
	@Autowired
	WeChatComponentAccessTokenDao weChatComponentAccessTokenDao;
	@Autowired
	RedisService redisService;

	/**
	 * 初始化 scheduledExecutorService
	 */
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

	/**
	 * 设置线程池
	 * @param poolSize poolSize
	 */
	public  void setPoolSize(int poolSize){
		this.poolSize = poolSize;
	}
	
	/**
	 * 设置线程方式
	 * @param daemon daemon
	 */
	public  void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
	public void initAll(){
		try {
			initComponentAccessToken(AuthorizeConsts.APP_ID, AuthorizeConsts.APP_SECRET_KEY);
			initPreAuthCode(AuthorizeConsts.APP_ID);
		}catch (Exception e){
			logger.error("COMPONENT_ACCESS_TOKEN bad initialization",e);
		}
	}
	public void initComponentAccessToken(String componentAppid, String componentSecret){
		initComponentAccessToken(componentAppid,componentSecret,0,118*60);

	}
	public void initPreAuthCode(String appid){
		initPreAuthCode(appid,60,570);
	}
	/**
	 * 初始化token 刷新，每118分钟刷新一次。
	 * @param appid appid
	 * @param secret secret
	 */
	public  void init(final String appid,final String secret){
		init(appid, secret, 0, 60*118);
	}

	/**
	 * 初始化token 刷新，每118分钟刷新一次。
	 * @param appid appid
	 * @param secret secret
	 * @param initialDelay 首次执行延迟（秒）
	 * @param delay 执行间隔（秒）
	 */
	public  void init(final String appid,final String secret,int initialDelay,int delay){
		if(scheduledExecutorService == null){
			initScheduledExecutorService();
		}

		if(futureMap.containsKey(appid)){
			futureMap.get(appid).cancel(true);
		}
		//立即执行一次
		if(initialDelay == 0){
			doRun(appid, secret);
		}
		ScheduledFuture<?> scheduledFuture =  scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				doRun(appid, secret);
			}
		},initialDelay == 0 ? delay : initialDelay,delay,TimeUnit.SECONDS);
		futureMap.put(appid, scheduledFuture);
		logger.info("ACCESS_TOKEN appid:{}",appid);
	}


	private  void doRun(final String appid,final String secret){
		try {

			Token token = TokenAPI.token(appid,secret);
			tokenMap.put(appid,token.getAccess_token());
			logger.info("ACCESS_TOKEN refurbish with appid:{}",appid);
		} catch (Exception e) {
			logger.error("ACCESS_TOKEN refurbish error with appid:{}",appid);
			logger.error("", e);
		}

	}
	public  void initPreAuthCode(final String appid,int initialDelay,int delay){
		if(scheduledExecutorService == null){
			initScheduledExecutorService();
		}
		if(futureMap.containsKey(appid+AuthorizeConsts.PRE_AUTH_CODE)){
			futureMap.get(appid+AuthorizeConsts.PRE_AUTH_CODE).cancel(true);
		}
		//立即执行一次
		if(initialDelay == 0){
			refreshPreAuthCode(appid);
		}
		ScheduledFuture<?> scheduledFuture =  scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				refreshPreAuthCode(appid);
			}
		},initialDelay == 0 ? delay : initialDelay,delay,TimeUnit.SECONDS);
		futureMap.put(appid+AuthorizeConsts.PRE_AUTH_CODE, scheduledFuture);
		logger.info("PRE_AUTH_CODE task start appid:{}",appid);

	}

	private  void refreshPreAuthCode(String appid){
		try {
			for(int i=0;i<10;i++){
				String componentAccessToken=redisService.get(AuthorizeConsts.APP_ID + AuthorizeConsts.COMPONENT_ACCESS_TOKEN);

				PreAuthCode preAuthCode = ComponentAPI.getApiPreauthcode(componentAccessToken, appid);
				if(preAuthCode.isSuccess()){
					redisService.set(appid+AuthorizeConsts.PRE_AUTH_CODE,preAuthCode.getPreAuthCode(),570);
					logger.info("PRE_AUTH_CODE refurbish with appid:{} and PRE_AUTH_CODE:{}",appid,preAuthCode.getPreAuthCode());
					break;
				}
				else{
					logger.error("PRE_AUTH_CODE HttpRequest ERROR with appid:{}",appid);
					Thread.sleep(10000);
				}
			}
		}catch (Exception e){
			logger.error("PRE_AUTH_CODE refurbish error with appid:{}",appid);
			logger.error("", e);
		}

	}

	private  void initComponentAccessToken(final String componentAppid, String componentSecret,int initialDelay,int delay) {

		if(scheduledExecutorService == null){
			initScheduledExecutorService();
		}

		if(futureMap.containsKey(componentAppid+AuthorizeConsts.COMPONENT_ACCESS_TOKEN)){
			futureMap.get(componentAppid+AuthorizeConsts.COMPONENT_ACCESS_TOKEN).cancel(true);
		}
		if(initialDelay == 0){
			refreshComponentAccessToken(componentAppid,componentSecret);
		}
		ScheduledFuture<?> scheduledFuture =  scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				refreshComponentAccessToken(componentAppid,componentSecret);
			}
		},initialDelay == 0 ? delay : initialDelay,delay,TimeUnit.SECONDS);
		futureMap.put(componentAppid+AuthorizeConsts.COMPONENT_ACCESS_TOKEN, scheduledFuture);
		logger.info("COMPONENT_ACCESS_TOKEN task start appid:{}",componentAppid);

	}

	private  void refreshComponentAccessToken(String appid,String secret){
		try{

			ComponentAccessToken componentAccessToken=null;
			String component_verify_ticket=null;
			for(int i=0;i<12;i++) {
				component_verify_ticket = weChatMsgAuthorizeDao.getLastestComponentVerifyTickets(AuthorizeConsts.APP_ID);
				if (component_verify_ticket == null) {
					logger.error("COMPONENT_ACCESS_TOKEN not exist in database ERROR with appid:{}",appid);
					Thread.sleep(60000);
				}else{
					componentAccessToken=ComponentAPI.getApiComponentToken(appid, secret, component_verify_ticket);
					if(componentAccessToken.isSuccess()){
						break;
					}else{
						logger.error("COMPONENT_ACCESS_TOKEN HttpRequest ERROR with appid:{}",appid);
						Thread.sleep(60000);
					};
				}
			}
			if (componentAccessToken.isSuccess()) {
				ComponentAccessTokenMsg componentAccessTokenMsg =new ComponentAccessTokenMsg(AuthorizeConsts.APP_ID,componentAccessToken.getComponentAccessToken(),componentAccessToken.getExpiresIn());
				weChatComponentAccessTokenDao.insertRecord(componentAccessTokenMsg);
				redisService.set(appid + AuthorizeConsts.COMPONENT_ACCESS_TOKEN, componentAccessToken.getComponentAccessToken(), 118 * 60);
				logger.info("COMPONENT_ACCESS_TOKEN refurbish with appid:{} and COMPONENT_ACCESS_TOKEN:{}",appid,componentAccessToken.getComponentAccessToken());

			}else{
				logger.error("COMPONENT_ACCESS_TOKEN HttpRequest ERROR with appid:{}",appid);
			}
		}catch (Exception e){
			logger.error("COMPONENT_ACCESS_TOKEN refurbish error with appid:{}",appid);
			logger.error("", e);
		}

	}


	/**
	 * 取消 token 刷新
	 */
	public  void destroyed(){
		scheduledExecutorService.shutdownNow();
		logger.info("destroyed");
	}
	
	/**
	 * 取消刷新
	 * @param appid appid
	 */
	public  void destroyed(String appid){
		if(futureMap.containsKey(appid)){
			futureMap.get(appid).cancel(true);
			logger.info("destroyed appid:{}",appid);
		}
	}

	/**
	 * 获取 access_token
	 * @param appid appid
	 * @return token
	 */
	public  String getToken(String appid){
		return tokenMap.get(appid);
	}

	/**
	 * 获取第一个appid 的 access_token
	 * 适用于单一微信号
	 * @return token
	 */
	public  String getDefaultToken(){
		return tokenMap.get(AuthorizeConsts.APP_ID);
	}

}
