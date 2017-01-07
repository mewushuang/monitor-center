angular.module('objUpdate').component('objUpdate', {

    templateUrl: '/app/obj-update/obj-update.template.html',
    controller: ['$scope', '$rootScope', '$routeParams', '$http', '$location', 'ObjService', 'pushService',
        function ($scope, $rootScope, $routeParams, $http, $location, ObjService, pushService) {
            var toUpdate;
            if ($routeParams.objId) {
                toUpdate = $rootScope.current[$routeParams.objId]
            }
            if (toUpdate) {
                $scope.id = toUpdate.id;
                $scope.name = toUpdate.name;
                $scope.url = toUpdate.url;
                $scope.ctrlname = toUpdate.ctrlname;
                $scope.params = angular.fromJson(toUpdate.params);
                $scope.canDelete = true;
            }
            if (!$scope.params || $scope.params.length == 0) {
                $scope.params = [{name: null, val: null}]
            }

            $scope.addParam = function () {
                $scope.params.push({name: null, val: null});
            };
            $scope.delParam = function (idx) {
                if ($scope.params.length == 1) return;
                $scope.params.splice(idx, 1);
            }
            $scope.doSubmit = function () {
                if (!$scope.updateObj.$valid) {
                    console.log($scope.updateObj.$error);
                    return;
                }
                var obj = {
                    id: $scope.id,
                    name: $scope.name,
                    url: $scope.url,
                    ctrlname: $scope.ctrlname,
                    params: angular.toJson($scope.params)
                };
                $http.post('/update', obj).then(function (resp) {//成功则返回
                    if (resp && resp.data&&resp.data.code == 200) {
                        pushService.handleMonitorObj(resp.data.data)
                        ObjService.refresh();
                        $location.path('/obj');
                    }
                    console.log(resp)
                }, function (resp) {
                    console.log(resp)
                });
            };

            $scope.deleteObj = function (objId) {
                $http.post('/obj/delete', {
                    objId: objId
                }).then(function (resp) {//成功则返回
                    if (resp && resp.status == 200) {
                        delete $rootScope.current[objId];
                        ObjService.refresh();
                        $location.path('#!/obj');
                    }
                    console.log(resp)
                }, function (resp) {
                    console.log(resp)
                });
            };

        }]
})