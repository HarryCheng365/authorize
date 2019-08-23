(function(window, angular, $) {

    var app = angular.module('myApp'['ui.router']);
    app.controller("indexCtrl", function ($scope, $http) {

        $scope.baseUrl = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?";
        $scope.mobileBaseUrl = "https://mp.weixin.qq.com/safe/bindcomponent?";
        $scope.component_appid = "component_appid";
        $scope.pre_auth_code = "pre_auth_code";
        $scope.redirect_url = "redirect_url";
        $scope.auth_type = "auth_type";
        $scope.getUrl = function ( flag) {
            $http({
                method: 'GET',
                url: '/authorize/componentAppId'
            }).then(function successCallback(response) {
                var data = response.data.parseJSON();
                var componentAppid = data.component_appid;
                var preAuthCode = data.pre_auth_code;
                var redirectUrl = data.redirect_url;
                var authType = data.auth_type;
                var realUrl;
                if(flag===true) {
                    realUrl = $scope.baseUrl + $scope.component_appid + "=" + componentAppid + "&" +
                        $scope.pre_auth_code + "=" + preAuthCode + "&" + $scope.redirect_url + "=" + redirectUrl + "&" +
                        $scope.auth_type + "=" + authType;
                }else{
                    realUrl=$scope.mobileBaseUrl + "action=bindcomponent" + "&" + $scope.auth_type + "=" + authType + "&no_scan=1" + "&" +
                    $scope.component_appid + "=" + componentAppid + "&" + $scope.pre_auth_code + "=" + preAuthCode + "&" + $scope.redirect_url + "=" + redirectUrl;
                }

                return realUrl;
            }, function errorCallback(response) {
                return null;

            });
        };
        $scope.goAuthorize = function () {
            var realUrl = $scope.getUrl(false);
            if (realUrl === null)
                window.alert("请求失败，请重试" + response.toString());
            else
                window.location.href = realUrl;
        }

    });



})(window,angular,jQuery);