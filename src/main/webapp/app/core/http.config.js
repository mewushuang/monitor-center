(function() {
    'use strict';

    angular
        .module('monitorApp')
        .config(httpConfig);

    httpConfig.$inject = [ '$httpProvider','$provide'];

    function httpConfig( $httpProvider,$provide) {


        $provide.factory('addContextNameInterceptor', function() {
            return {
                // optional method
                'request': function(config) {
                    if(config.url) config.url=angular.___context_name___+config.url;
                    return config;
                },


            };
        });
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
        $httpProvider.interceptors.push('addContextNameInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');
    }
})();


