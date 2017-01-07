angular.
module('objInfo',['ngRoute','chart.js','ui.bootstrap','core.obj']);

angular.module('objInfo')
    .filter('byGroup', function() {
        return function(input,group) {
            if (!input) return [];
            if(group=='ALL') return input;
            var result = [];
            angular.forEach(input, function(value, key) {
                var idx=value.indexOf(group);
                if (idx==0||idx==1) {
                    result.push(value);
                }
            });
            return result;
        }
    });