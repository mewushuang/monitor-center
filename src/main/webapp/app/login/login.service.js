(function() {
    'use strict';

    angular
        .module('monitorApp')
        .factory('loginService', LoginService);

    LoginService.$inject = ['$uibModal'];

    function LoginService ($uibModal) {
        var service = {
            open: open
        };

        var modalInstance = null;
        var resetModal = function () {
            modalInstance = null;
        };

        return service;

        function open () {
            if (modalInstance !== null) return;
            //resolve：定义一个成员并将他传递给$modal指定的控制器，相当于routes的一个reslove属性，如果需要传递一个objec对象，需要使用angular.copy()
            //backdrop：控制背景，允许的值：true（默认），false（无背景），“static” - 背景是存在的，但点击模态窗口之外时，模态窗
            modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/login/login.template.html',
                controller: 'loginController',
                controllerAs: 'vm',
                backdrop:'static',
                keyboard:false,//当按下Esc时，模态对话框是否关闭，默认为ture
                resolve: {
                    $modalInstance:function () {
                        return modalInstance;
                    }
                }
            });
            modalInstance.result.then(
                resetModal,
                resetModal
            );
        }
    }
})();
