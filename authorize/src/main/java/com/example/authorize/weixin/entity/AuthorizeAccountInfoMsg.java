package com.example.authorize.weixin.entity;

public class AuthorizeAccountInfoMsg {

    private String tid;
    private String authorizerAppId;
    private String user_name;
    private String nick_name;
    private String principal_name;
    private String head_img;
    private Integer service_type_info;
    private Integer verify_type_info;
    private String alias;
    private String business_info;
    private String qrcode_url;
    private String func_info;


    public AuthorizeAccountInfoMsg(String tid,String authorizerAppId,String user_name,String nick_name,String principal_name,String head_img,
                                   Integer service_type_info,Integer verify_type_info,String alias,String business_info,String qrcode_url,String func_info){
        this.tid=tid;
        this.authorizerAppId =authorizerAppId;
        this.user_name=user_name;
        this.nick_name=nick_name;
        this.principal_name=principal_name;
        this.head_img=head_img;
        this.service_type_info =service_type_info;
        this.verify_type_info=verify_type_info;
        this.alias=alias;
        this.business_info=business_info;
        this.qrcode_url=qrcode_url;
        this.func_info=func_info;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public String getUser_name() {
        return user_name;
    }

    public Integer getService_type_info() {
        return service_type_info;
    }

    public Integer getVerify_type_info() {
        return verify_type_info;
    }

    public String getHead_img() {
        return head_img;
    }

    public String getPrincipal_name() {
        return principal_name;
    }

    public String getBusiness_info() {
        return business_info;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setPrincipal_name(String principal_name) {
        this.principal_name = principal_name;
    }

    public String getAlias() {
        return alias;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getFunc_info() {
        return func_info;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setVerify_type_info(Integer verify_type_info) {
        this.verify_type_info = verify_type_info;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setService_type_info(Integer service_type_info) {
        this.service_type_info = service_type_info;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setBusiness_info(String business_info) {
        this.business_info = business_info;
    }

    public void setFunc_info(String func_info) {
        this.func_info = func_info;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}

