angular.module('scode').filter('trunc', function () {
    return function (input) {
        return input.slice(0, input.indexOf(":"))
    }
})

angular.module('scode').component('scode', {
    templateUrl: '/app/scode-monitor/scode-monitor.template.html',
    controller: [
        '$http',
        'ObjService',
        '$location',
        'loginService',
        '$scope',
        '$route',
        function ($http, ObjService, $location, loginService, $scope, $route) {
            var self = this;
            self.orderPorp = 'id';//根据id排序
            self.fileName = '';
            //TODO 异常处理
            $http.get('/scode/list').then(function (response) {
                    self.scodes=response.data;
                    //self.objs=response.data.data;
                    //sc.findAll(pushService.handleList)
                }, function errorhandler(resp) {
                    console.log(resp);
                }
            )
            self.getClass=function (interval) {
                if(interval>1){
                    return "danger"
                }
            }


        }]
})