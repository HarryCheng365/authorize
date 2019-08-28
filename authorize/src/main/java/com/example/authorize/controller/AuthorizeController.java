package com.example.authorize.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.authorize.weixin.api.ComponentAPI;
import com.example.authorize.weixin.consts.AuthorizeConsts;
import com.example.authorize.weixin.service.MsgAuthorizeService;
import com.example.authorize.weixin.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@RestController
@CrossOrigin
@RequestMapping("/authorize")
public class AuthorizeController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizeController.class);
    @Autowired
    MsgAuthorizeService msgAuthorizeService;


    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ModelAndView index(){return new ModelAndView("index");}

    @RequestMapping(value = "/event",method = RequestMethod.POST)
    public void getAuthorizeEvent(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)throws IOException, URISyntaxException {

        try {
            String body = IOUtils.getRequestPostStr(httpServletRequest);
            String result=msgAuthorizeService.parseComponentTicket(body);
            IOUtils.sendOutputStr(result,httpServletResponse);
        }catch (Exception e){
            LOG.error("HttpServletRequest 解析失败"+e.getMessage());
        }

    }

    @RequestMapping(value="/authorizeUrl",method = RequestMethod.POST)
    public String getAuthorizeUrl(@RequestBody String json){
        JSONObject result =new JSONObject();
        try {
            JSONObject jsonObject= JSON.parseObject(json);
            String tid=jsonObject.getString("tid");
            if (jsonObject.getString("methodType").equals("QR_CODE")) {
                result.put("url",ComponentAPI.getAuthUrlScan(AuthorizeConsts.appId, msgAuthorizeService.getPreAuthCode(AuthorizeConsts.appId),genRedirectUrl(AuthorizeConsts.redirect_url,tid),"3" ));

            } else {
                result.put("url", ComponentAPI.getAuthUrl(AuthorizeConsts.appId, msgAuthorizeService.getPreAuthCode(AuthorizeConsts.appId),genRedirectUrl(AuthorizeConsts.redirect_url,tid),"3" ));
            }
        }catch (Exception e){
            LOG.error("AuthUrl 生成失败",e);
            JSONObject error = new JSONObject();
            error.put("errorcode","503");
            return error.toJSONString();
        }
        return result.toJSONString();

    }


    /**
     *
     * auth_type 解释
     * 要授权的帐号类型：1则商户点击链接后，手机端仅展示公众号、2表示仅展示小程序，
     * 3表示公众号和小程序都展示。如果为未指定，则默认小程序和公众号都展示。第三方平台开发者可以使用本字段来控制授权的帐号类型。
     */
    @RequestMapping(value="/componentAppId",method= RequestMethod.GET)
    public String  getAuthUrl(){

        String result=null;
        try {
            JSONObject response = new JSONObject();
            response.put("component_appid", AuthorizeConsts.appId);
            response.put("pre_auth_code", msgAuthorizeService.getPreAuthCode(AuthorizeConsts.appId));
            response.put("redirect_url", changeCharset(AuthorizeConsts.redirect_url, "utf-8"));
            response.put("auth_type", "3");
            result=response.toJSONString();

        }catch (Exception e){
            LOG.error("AuthUrl 转换失败",e);
            JSONObject error = new JSONObject();
            error.put("errorcode","503");
            return error.toJSONString();
        }
        return result;

    }

    @RequestMapping(value="/finish/{tid}",method = RequestMethod.GET)
    public ModelAndView getAuthCode(@PathVariable("tid")String tid, @RequestParam("auth_code") String auth_code,@RequestParam("expires_in") int expires_in){

        try {
            msgAuthorizeService.saveUserAccessToken(tid, auth_code, expires_in);

        }catch (Exception e){
            LOG.error("根据auth_code 获取 access_token发生错误: ",e);
        }

        return new ModelAndView("finish");
    }



    public String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            //用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }

    private String genRedirectUrl(String baseUrl,String appendix){
        String result="";
        try {
             result= baseUrl + appendix;
            result = changeCharset(result, "utf-8");
        }catch (Exception e){
            LOG.error("AuthUrl 转换失败",e);
        }
        return result;


    }
}
