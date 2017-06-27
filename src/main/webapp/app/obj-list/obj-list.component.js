angular.module('objList').filter('trunc',function () {
    return function(input){
        return input.slice(0,input.indexOf(":"))
    }
})

angular.module('objList').component('objList', {
    templateUrl: '/app/obj-list/obj-list.template.html',
    controller: [
        '$http',
        'ObjService',
        '$location',
        'loginService',
        '$scope',
        '$route',
        function ($http, ObjService, $location,loginService,$scope,$route) {
            var self = this;
            self.orderPorp = 'id';//根据id排序
            self.fileName = '';
            //TODO 异常处理

            //启动服务
            self.startObj = ObjService.startRemotely;

            //停止服务
            self.stopObj =function (objId,args) {
                ObjService.stopRemotely(objId,args)
            };

            self.reloadFromDB = ObjService.reload;

            self.showInfo=function (objId,param) {

                $location.path('/info/'+objId);
            }

            self.scode=function () {

                $location.path('/scode');
            }

            self.logout=function () {
                $http.post('/api/logout',{}).then(
                    function (resp) {
                        if(resp&&resp.status==200){
                            loginService.open();
                        }
                    },function (resp) {
                        console.log(resp);
                    }
                )
            }


        }]
})