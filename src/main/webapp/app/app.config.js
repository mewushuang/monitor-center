'use strict'
angular.
module('monitorApp').
config(['$locationProvider',
    '$routeProvider','$provide','$httpProvider',
    'ChartJsProvider',
    function config($locationProvider,$routeProvider,$provide,$httpProvider,ChartJsProvider){
        $locationProvider.hashPrefix('!');

        $routeProvider.
        when('/obj', {
            template: '<obj-list></obj-list>'
        }).
        when('/update/:objId', {
            template: '<obj-update></obj-update>'
        }).
        when('/info/:objId', {
            template: '<obj-info></obj-info>'
        }).
        when('/scode', {
            template: '<scode></scode>'
        }).
        otherwise('/obj');





        ChartJsProvider.setOptions({ colors : [ '#803690', '#00ADF9', '#DCDCDC', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360'] });
    }]);