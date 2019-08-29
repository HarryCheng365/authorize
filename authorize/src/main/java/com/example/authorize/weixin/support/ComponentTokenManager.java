package com.example.authorize.weixin.support;


import com.example.authorize.weixin.entity.ApiQueryAuthResult;

/**
 * ComponentTokenManager 第三方平台token 管理
 * 
 * @author Haoyu
 * @date 2019/8/21
 */
public interface ComponentTokenManager {

	/**
	 * 获取 component_access_token
	 * 
	 * @param component_appid component_appid
	 * @return COMPONENT_ACCESS_TOKEN
	 */
	String getComponentAccessToken(String component_appid);

	/**
	 * 获取 component_access_token <br>
	 * 适用于单一第三方平台环境
	 * 
	 * @return COMPONENT_ACCESS_TOKEN
	 */
	String getComponentAccessToken();

	/**
	 * 获取 authorizer_access_token
	 * 
	 * @param component_appid component_appid
	 * @param authorizer_appid authorizer_appid
	 * @return authorizerAccessToken
	 */
	String getAuthorizerAccessToken(String component_appid, String authorizer_appid);

	/**
	 * 获取 authorizer_access_token<br>
	 * 适用于单一第三方平台环境
	 * 
	 * @param authorizer_appid authorizer_appid
	 * @return authorizerAccessToken
	 */
	String getAuthorizerAccessToken(String authorizer_appid);

	/**
	 * 刷新 component_access_token
	 * 
	 * @param component_appid component_appid
	 * @param component_appsecret component_appsecret
	 * @param component_verify_ticket component_verify_ticket
	 */
	void component_verify_ticket(String component_appid, String component_appsecret, String component_verify_ticket);

	/**
	 * 授权事件
	 * 
	 * @param component_appid component_appid
	 * @param authorizationCode authorizationCode
	 * @return ApiQueryAuthResult
	 */
	ApiQueryAuthResult authorized(String component_appid, String authorizationCode);

	/**
	 * 取消授权事件
	 * 
	 * @param component_appid component_appid
	 * @param authorizer_appid authorizer_appid
	 */
	void unauthorized(String component_appid, String authorizer_appid);
}
