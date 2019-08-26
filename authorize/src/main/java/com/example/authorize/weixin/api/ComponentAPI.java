package com.example.authorize.weixin.api;

import com.example.authorize.weixin.client.BaseResult;
import com.example.authorize.weixin.client.LocalHttpClient;
import com.example.authorize.weixin.entity.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class ComponentAPI extends BaseAPI{


    private static Logger logger = LoggerFactory.getLogger(ComponentAPI.class);


    /**
     * 获取公众号第三方平台component_access_token
     *
     * @param component_appid         公众号第三方平台appid
     * @param component_appsecret     公众号第三方平台appsecret
     * @param component_verify_ticket 微信后台推送的ticket，此ticket会定时推送，具体请见本页末尾的推送说明
     * @return 公众号第三方平台access_token
     */
    public static ComponentAccessToken getApiComponentToken(String component_appid, String component_appsecret, String component_verify_ticket) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\" ,\"component_appsecret\": \"%2$s\",\"component_verify_ticket\": \"%3$s\"}",
                component_appid,
                component_appsecret,
                component_verify_ticket);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_component_token")
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, ComponentAccessToken.class);
    }

    /**
     * 获取预授权码
     *
     * @param component_access_token component_access_token
     * @param component_appid        公众号第三方平台appid
     * @return 预授权码
     */
    public static PreAuthCode getApiPreauthcode(String component_access_token, String component_appid) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\"}",
                component_appid);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_create_preauthcode")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, PreAuthCode.class);
    }

    /**
     * 生成移动端快速授权URL
     * @since 2.8.22
     * @param component_appid 第三方平台ID
     * @param pre_auth_code   预授权码
     * @param redirect_uri    重定向URI
     * @param auth_type       要授权的帐号类型 <br>
     *                        1 则商户扫码后，手机端仅展示公众号 <br>
     *                        2 表示仅展示小程序 <br>
     *                        3 表示公众号和小程序都展示。<br>
     *                        如果为未制定，则默认小程序和公众号都展示。第三方平台开发者可以使用本字段来控制授权的帐号类型。
     * @return URL
     */
    public static String getAuthUrl(String component_appid, String pre_auth_code, String redirect_uri, String auth_type) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(MP_URI + "/safe/bindcomponent?")
                    .append("action=").append("bindcomponent")
                    .append("&auth_type=").append(auth_type)
                    .append("&no_scan=").append("1")
                    .append("&component_appid=").append(component_appid)
                    .append("&pre_auth_code=").append(pre_auth_code)
                    .append("&redirect_uri=").append(URLEncoder.encode(redirect_uri, "utf-8"));

            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public static String getAuthUrlScan(String component_appid,String pre_auth_code,String redirect_uri,String auth_type){
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(MP_URI + "cgi-bin/componentloginpage?")
                    .append("&component_appid=").append(component_appid)
                    .append("&pre_auth_code=").append(pre_auth_code)
                    .append("&redirect_url=").append(URLEncoder.encode(redirect_uri, "utf-8"))
                    .append("&auth_type=").append(auth_type);
            return sb.toString();
        }catch(UnsupportedEncodingException e){
            logger.error("",e);
            throw new RuntimeException(e);
        }

    }



    /**
     * 使用授权码换取公众号的授权信息
     *
     * @param component_access_token component_access_token
     * @param component_appid        公众号第三方平台appid
     * @param authorization_code     授权code,会在授权成功时返回给第三方平台，详见第三方平台授权流程说明
     * @return 公众号的授权信息
     */
    public static ApiQueryAuthResult api_query_auth(String component_access_token, String component_appid, String authorization_code) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\",\"authorization_code\":\"%2$s\"}",
                component_appid,
                authorization_code);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_query_auth")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, ApiQueryAuthResult.class);
    }



    /**
     * 获取授权方的账户信息
     *
     * @param component_access_token component_access_token
     * @param component_appid        服务appid
     * @param authorizer_appid       授权方appid
     * @return 授权方的账户信息
     */
    public static ApiGetAuthorizerInfoResult api_get_authorizer_info(String component_access_token,
                                                                     String component_appid,
                                                                     String authorizer_appid) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\",\"authorizer_appid\":\"%2$s\"}",
                component_appid, authorizer_appid);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_get_authorizer_info")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, ApiGetAuthorizerInfoResult.class);
    }


    /**
     * 获取（刷新）授权公众号的令牌
     *
     * @param component_access_token   component_access_token
     * @param component_appid          公众号第三方平台appid
     * @param authorizer_appid         授权方appid
     * @param authorizer_refresh_token 授权方的刷新令牌，刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。 一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
     * @return 授权公众号的令牌
     */
    public static AuthorizerAccessToken api_authorizer_token(String component_access_token,
                                                             String component_appid,
                                                             String authorizer_appid,
                                                             String authorizer_refresh_token) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\",\"authorizer_appid\":\"%2$s\",\"authorizer_refresh_token\":\"%3$s\"}",
                component_appid, authorizer_appid, authorizer_refresh_token);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_authorizer_token")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, AuthorizerAccessToken.class);
    }

    /**
     * 第三方平台对其所有API调用次数清零
     *
     * @param component_access_token 调用接口凭据
     * @param component_appid        第三方平台APPID
     * @return result
     * @since 2.8.2
     */
    public static BaseResult clearQuota(String component_access_token, String component_appid) {
        String json = String.format("{\"component_appid\":\"%s\"}", component_appid);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/clear_quota")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(json, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }


    /**
     *
     * @param componentAppid
     * @param authorizer_appid
     * @param tid
     * @return
     */
    public static BaseResult authorizeFinish(String componentAppid,String authorizer_appid,String tid ){
        String json="";
        if(authorizer_appid==null){
            json=String.format("{\"errorcode\":\"%1$s\",\"msg\":\"%2$s\",\"tid\":\"%3$s\"}","503","未授权成功",tid);
        }else{
            json=String.format("{\"component_appid\":\"%1$s\",\"authorizer_appid\":\"%2$s\",\"tid\":\"%3$s\"}",componentAppid,authorizer_appid,tid);
        }
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri("")
                .setEntity(new StringEntity(json,Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest,BaseResult.class);

    }



    /**
     * 拉取当前所有已授权的帐号基本信息
     *
     * @param component_access_token component_access_token
     * @param component_appid        第三方平台APPID
     * @param offset                 偏移位置/起始位置
     * @param count                  拉取数量，最大为500
     * @return ApiGetAuthorizerListResult
     */
    public static ApiGetAuthorizerListResult getAuthorizeList(String component_access_token, String component_appid, String offset, String count) {
        String postJsonData = String.format("{\"component_appid\":\"%1$s\",\"offset\":\"%2$s\",\"count\":\"%3$s\"}",
                component_appid, offset, count);

        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/component/api_get_authorizer_list")
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .setEntity(new StringEntity(postJsonData, Charset.forName("utf-8")))
                .build();

        return LocalHttpClient.executeJsonResult(httpUriRequest, ApiGetAuthorizerListResult.class);

    }
}
