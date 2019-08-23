CREATE database if NOT EXISTS `authorize` default character set utf8 collate utf8_general_ci;
use `authorize`;

create table if NOT EXISTS `wechat_authorizer_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appId` varchar(32) NOT NULL comment '第三方平台appid',
  `createTime` bigint(20) NOT NUll comment '时间戳',
  `infoType` varchar(32) default null comment '授权消息类型',
  `componentVerifyTicket` varchar(128)  comment '平台校验ticket',
  `authorizerAppId` varchar(64) default null comment '公众号或小程序',
  `authorizationCode` varchar(128) default null comment '授权码，可用于换取公众号的接口调用凭据',
  `authorizationCodeExpiredTime` int(11) default 0 comment '授权码过期时间',
  `preAuthCode` varchar(128) default null comment '预授权码',
  PRIMARY KEY (`id`)
  ) engine=innodb default charset=utf8;

create table if NOT EXISTS `wechat_component_access_tokens` (
  `id` int(11) NOT NULL auto_increment comment '自增id',
  `componentAppId` varchar(32) NOT NULL comment '第三方平台appId',
  `componentAccessToken` varchar(128) default null,
  `expiresIn` int(11) default 0,
  `createTime` bigint(20) default 0,
  PRIMARY KEY (`id`)
  ) engine=innodb default charset=utf8;


create table if NOT EXISTS `wechat_user_access_tokens`(
  `authorizerAppId` varchar(64) NOT NULL comment '公众号或小程序的appId',
  `authorizerAccessToken` varchar(128) default null comment
  '授权方接口调用凭据（在授权的公众号或小程序具备api权限时，才有此返回值），也简称为令牌',
  `authorizerRefreshToken` varchar(64) default null comment
  '接口调用凭据刷新令牌',
  `expiresIn` int(11) default 0 comment
  '有效期（在授权的公众号或小程序具备api权限时，才有此返回值）',
  `createTime` bigint(20) default 0 comment'创建时间',
  PRIMARY KEY (`authorizerAppId`)
  ) engine=innodb default charset=utf8;

create table if NOT EXISTS `wechat_user_account_info` (
  `authorizerAppId` varchar (64) NOT NULL comment '授权方公众号appId',
  `user_name` varchar(64) default null comment '授权方公众号的原始ID',
  `nick_name` varchar(64) default null comment '授权方昵称 微信SDK Demo Special',
  `principal_name` varchar(64) default null comment '公众号的主体名称',
  `head_img` varchar(128) default null comment '公众号主体头像http://wx.qlogo.cn/mmopen/GPy',
  `service_type_info` int(11) default 0 comment '授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号',
  `verify_type_info` int(11) default 0 comment '授权方认证类型',
  `alias` varchar(64) default null comment '授权方公众号所设置的微信号，可能为空',
  `business_info` varchar(128) default null comment
  '用以了解以下功能的开通状况（0代表未开通，1代表已开通）： open_store:是否开通微信门店功能 open_scan:是否开通微信扫商品功能open_pay:是否开通微信支付功能 open_card:是否开通微信卡券功能 open_shake:是否开通微信摇一摇功能',
  `qrCode_url` varchar(128) default null comment '二维码图片的URL，开发者最好自行也进行保存',
  `func_info` varchar(128) default null comment'公众号授权给开发者的权限集列表',
  PRIMARY KEY (`authorizerAppId`)
  ) engine=innodb default charset=utf8;

create INDEX msgIndex on wechat_authorizer_msg(createTime);

commit;

