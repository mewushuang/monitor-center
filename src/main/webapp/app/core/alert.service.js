angular.module('monitorApp')
    .factory('alertService',[
        //'$resource',
        '$uibModal','pushService',
        function ($uibModal,pushService) {
            var sc = {
                alertWarn:warn
            };
            return sc;
            var modalInstance;
            function warn(msg, accept,deny) {
                modalInstance= $uibModal.open({
                    animation: true,
                    template:
                    '<div class="panel-body text-center" style="height: 200px">' +
                    '<div style="margin: 50px 10px"><span class="text-center">{{notice}}</span></div>'+
                    '<div class="form-group"></div><button ng-click="dismiss()" class="btn btn-default" style="margin: 0 50px;">取消</button>'+
                    '<button ng-click="confirm()" class="btn btn-default" style="margin: 0 50px;">确定</button></div>'+
                    '</div>',
                    controller:['$uibModalInstance','$scope',function ($uibModalInstance,$scope) {
                        $scope.notice=$scope.$resolve.notice;
                        $scope.confirm=function () {
                            $uibModalInstance.close(true);
                        };
                        $scope.dismiss=function () {
                            $uibModalInstance.dismiss();
                        }
                    }],
                    controllerAs:'ctrl',
                    resolve: {
                        notice:function () {
                            return msg;
                        }
                    }
                });

                modalInstance.result.then(function (result) {
                    if(result&&accept){
                        accept();
                    }
                }, function () {
                    if(deny){
                        deny();
                    }
                    console.log('modal-component dismissed at: ' + new Date());
                });
            }
        }]);

