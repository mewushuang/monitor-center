(function() {
    function objShotController($scope) {
        $scope.labels = ['CPU', 'MEMORY', 'DISK'];
        //$scope.labels = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
        $scope.series = ['Series A'];

        $scope.data = [
            [65, 55, 50],
            //[28, 27, 90]
        ];
    }

    objShotController.$inject = ['$scope'];

    angular.module('objShot').component('objShot', {

        templateUrl: '/app/obj-shot/obj-shot.template.html',
        controller: objShotController
    })
})();
