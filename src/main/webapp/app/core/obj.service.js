angular.module('core.obj',['ngRoute','core.push']);
angular.module('core.obj')
    .filter('toArray', function() {
        return function(input) {
            if (!input) return [];
            var result = [];
            angular.forEach(input, function(value, key) {
                if (key&&value) {
                    result.push(value);
                }
            });
            return result;
        }
    });
angular.module('core.obj')
    .factory('ObjService',[
        //'$resource',
        '$http','$location','pushService',
        function ($http,$location,pushService) {
            var sc={};
            sc.objId=undefined;


            sc.startRemotely=function (objId,args) {
                //var cb = callback || angular.noop;
                console.log('starting service');
                $http.post('/obj/start',{objId:objId,args:args}).then(function (response) {
                    console.log(response.data)
                    //self.objs=response.data.data;
                    //sc.findAll(pushService.handleList)
                },errorhandler)
            };
            sc.stopRemotely=function (objId,args) {
                //var cb = callback || angular.noop;
                console.log('stopping service id['+objId+']');
                $http.post('/obj/stop',{objId:objId,args:args}).then(function (response) {
                    console.log(response.data);
                    //self.objs=response.data.data;
                    //sc.findAll(pushService.handleList)
                },errorhandler)
            };
            sc.restart=function(objId){
                console.log('restarting service id['+objId+']');
                $http.post('/obj/restart',{objId:objId,args:""}).then(function (response) {
                    console.log(response.data);
                    //self.objs=response.data.data;
                    //sc.findAll(pushService.handleList)
                },errorhandler)
            };
            function errorhandler(resp) {
                console.log(resp);
            }

            sc.findAll=function (callback) {
                var cb = callback || angular.noop;
                $http.get('/obj/list?info='+new Date().getMilliseconds()).then(cb,errorhandler) ;
            };
            sc.refresh=function(){
                sc.findAll(pushService.handleMonitorObj);
            };

            sc.logFiles=function (objId,callback) {
                sc.objId=objId;
                var cb = callback || angular.noop;
                $http.get('/obj/fileList?objId='+objId).then(cb,errorhandler) ;

            };
            sc.parentPath=function (objId,callback) {
                sc.objId=objId;
                var cb = callback || angular.noop;
                $http.get('/file/parent?objId='+objId).then(cb,errorhandler) ;

            };

            sc.reload=function (callback) {
                var cb = callback || angular.noop;
                $http.get('/obj/reload').then(cb,errorhandler);
            };

            sc.getNextLines=function (fileName, lineNum,callback) {
                sc.fileName=fileName||sc.fileName;
                var cb = callback || angular.noop;
                $http.get('/obj/log/next?objId='+sc.objId+'&fileName='+sc.fileName+'&lineNum='+lineNum)
                    .then(cb,errorhandler);
            };

            sc.deleteLog=function (fileName,callback) {
                //sc.fileName=fileName||sc.fileName;
                var cb = callback || angular.noop;
                $http.get('/obj/log/delete?objId='+sc.objId+'&fileNames='+fileName)
                    .then(cb,errorhandler);
            };
            sc.download=function (fileName) {
                //sc.fileName=fileName||sc.fileName;
                window.location=angular.___context_name___+'/file/download?objId='+sc.objId+'&fileName='+fileName;
                //$http.get('/file/download?objId='+sc.objId+'&fileNames='+fileName)
                    //.then(angular.noop,errorhandler);
            };

            sc.searchByPattern=function (pattern,callback) {
                var cb = callback || angular.noop;
                $http.get('/obj/log/patternSearch?objId='+sc.objId+'&fileName='+sc.fileName+'&pattern='+pattern)
                    .then(cb,errorhandler);
            };

            sc.searchByPercent=function (percent,callback) {
                var cb = callback || angular.noop;
                $http.get('/obj/log/percentSearch?objId='+sc.objId+'&fileName='+sc.fileName+'&percent='+percent)
                    .then(cb,errorhandler);
            };

            sc.resetLogOffset=function (callback) {
                var cb = callback || angular.noop;
                $http.get('/obj/log/reset?objId='+sc.objId+'&fileName='+sc.fileName)
                    .then(cb,errorhandler);
            };


            return sc;
        }]);

