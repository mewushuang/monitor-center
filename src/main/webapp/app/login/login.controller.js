(function () {
    angular.module('login').controller('loginController', loginController);

    loginController.$inject = ['$scope', '$rootScope', '$uibModalInstance', '$http', '$location'];
    function loginController($scope, $rootScope, $uibModalInstance, $http, $location) {
        $scope.keyup=function (event) {
            if(event.keyCode=='13'){
                $scope.doSubmit();
            }
        }

        $scope.doSubmit = function () {
            if (!$scope.loginObj.$valid) {
                console.log($scope.loginObj.$error);
                return;
            }
            var credentials = {
                username: $scope.j_username,
                password: $scope.j_password,
            };
            login(credentials).then(function (resp) {//成功则返回
                if (resp && resp.status == 200) {
                    $location.path('/obj');
                    $http.get('/');//临时解决方案：重新获取_csrf token
                    $uibModalInstance.close();
                }
                console.log(resp)
            }, function (resp) {
                console.log(resp)
            });
        };

        function login(credentials) {
            var data = 'j_username=' + encodeURIComponent(credentials.username) +
                '&j_password=' + encodeURIComponent(credentials.password) +
                '&submit=Login';

            return $http.post('/api/authentication', data, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (response) {
                return response; 
            });
        }
    }

})();
