'use strict';

// Define the `phonecatApp` module
angular.module('monitorApp', [
    // ...which depends on the `phoneList` module
    'ngRoute',
    'ui.bootstrap',
    'chart.js',
    'ngStomp',
    'ngFileUpload',
    'objList',
    'objInfo',
    'login',
    'objUpdate',
    'core.obj',
    'core.push',
    'scode'
]);
//直接部署在容器（tomcat)根目录下时没有问题，http请求类似127.0.0.1:8080/index.html
//但入部署为tomcat的某个应用，http请求仍为上面的路径，但此时实际路径已变成127.0.0.1:8080/appName/index.html
//此处添加一个interceptor，用来为所有http请求前添加应用名
var contextName='';//"monitor-center";
if(window.___context___path) contextName=window.___context___path;
angular.___context_name___=contextName;
angular.module('monitorApp')
    .controller('initWS', ['$scope', 'pushService','$rootScope','ObjService',
    function($scope, pushService,$rootScope,ObjService) {
        $scope.load = function() {
            //初始化current对象
            $rootScope.current={};
            pushService.initConnect();
            ObjService.findAll(pushService.handleList)
        }
    }]);

