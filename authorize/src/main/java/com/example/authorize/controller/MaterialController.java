package com.example.authorize.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.authorize.weixin.service.MaterialService;
import com.example.authorize.weixin.service.MaterialTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/material")
public class MaterialController {
    private static final Logger LOG = LoggerFactory.getLogger(MaterialController.class);

    @Autowired
    MaterialService materialService;

    @RequestMapping(value="/getAccessToken",method= RequestMethod.POST)
    public String getUserAccessToken(@RequestParam("authorizer_appid") String authorizer_appid){
        String result=null;
        try{

           result= materialService.getUserAccessToken(authorizer_appid);
        }catch (Exception e){
            LOG.error("根据id获取Authorizer_Access_Token 失败");
            JSONObject error=new JSONObject();
            error.put("errorcode","503");
            error.put("msg","根据id获取Authorizer_Access_Token 失败");
            result=error.toJSONString();
        }
        return result;

    }
}
