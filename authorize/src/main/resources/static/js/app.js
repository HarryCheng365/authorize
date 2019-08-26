(function(angular, $) {
    var app=angular.module('myApp', ['ngFileUpload','ui.router','ngImgCrop']);

    app.run( function run( $http ){

        // For CSRF token compatibility with Django
        $http.defaults.headers.post['X-CSRFToken'] = "{{ csrf_token }}";

    });

    app.config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/index');
        $stateProvider.state('index',{
            url:'/index',
            views:{
                '':{
                    templateUrl:'page/main.html'
                }
            }

        }).state('error',{
            url:'/error',
            views:{
                '':{
                    templateUrl:'page/errorpage.html'
                }
            }
        });

    });

    app.controller('indexCtrl', ['$scope','$http','$state',function ($scope,$http,$state){//必须 引入和

        $scope.deviceFlag=true;
        $scope.init=function(){


            var sUserAgent = navigator.userAgent.toLowerCase();
            var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
            var bIsIphoneOs = sUserAgent.match(/iphone os/i) =="iphone os";
            var bIsMidp = sUserAgent.match(/midp/i) == "midp";
            var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
            var bIsUc = sUserAgent.match(/ucweb/i) === "ucweb";
            var bIsAndroid = sUserAgent.match(/android/i) == "android";
            var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
            var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
            if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
               $scope.deviceFlag=true;
            } else {
                $scope.deviceFlag=false;
            }

        };


        $scope.paramsList=[];


        $scope.baseUrl = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?";
        $scope.mobileBaseUrl = "https://mp.weixin.qq.com/safe/bindcomponent?";
        $scope.component_appid = "component_appid";
        $scope.pre_auth_code = "pre_auth_code";
        $scope.redirect_url = "redirect_url";
        $scope.auth_type = "auth_type";
        $scope.realUrl="";
        $scope.errorMessage="";
        $scope.getUrl = function ( flag) {
            $http({
                method: 'GET',
                url: '/authorize/componentAppId'
            }).then(function successCallback(response) {
                $scope.errorMessage=response.data.errorcode;
                var componentAppid = response.data.component_appid;
                var preAuthCode = response.data.pre_auth_code;
                var redirectUrl = response.data.redirect_url;
                var authType = response.data.auth_type;
                var realUrl;
                if(flag===true) {
                    realUrl = $scope.baseUrl + $scope.component_appid + "=" + componentAppid + "&" +
                        $scope.pre_auth_code + "=" + preAuthCode + "&" + $scope.redirect_url + "=" + redirectUrl + "&" +
                        $scope.auth_type + "=" + authType;
                }else{
                    realUrl=$scope.mobileBaseUrl + "action=bindcomponent" + "&" + $scope.auth_type + "=" + authType + "&no_scan=1" + "&" +
                        $scope.component_appid + "=" + componentAppid + "&" + $scope.pre_auth_code + "=" + preAuthCode + "&" + $scope.redirect_url + "=" + redirectUrl;
                }

                if($scope.errorMessage!=null&&$scope.errorMessage!==""&&$scope.errorMessage!==undefined)
                    $scope.goErrorPage();
                else{
                    window.location.href = realUrl;
                }
                }, function errorCallback(response) {
                $scope.errorMessage=response.data;
                $scope.goErrorPage();

            });
        };
        $scope.goAuthorize = function () {
            $scope.getUrl(false);
        };

        $scope.goErrorPage =function(){
            $state.go('error', {chosenData: null}, {reload: true})
        };


    }]);

})(angular, jQuery);