angular.module('core.push', ['ngStomp']);
angular.module('core.push')
    .factory('pushService', [
        '$stomp', '$rootScope','$http',
        function ($stomp, $rootScope,$http) {
            var sc = {};

            var parameHandle=function(paramedService){

                paramedService.warnClass = "btn-warning";
                paramedService.startStatus = true;
                paramedService.stopStatus = true;
                paramedService.infoStatus = false;
                if (!paramedService.metrics||!paramedService.metrics.runningStatus) {
                    paramedService.infoStatus = true;
                } else if (paramedService.metrics.runningStatus.value == 'running') {
                    paramedService.warnClass="btn-success"
                    paramedService.stopStatus = false;
                } else if (paramedService.metrics.runningStatus.value == 'stopped') {
                    paramedService.warnClass="btn-warning"
                    paramedService.startStatus = false;
                }else if(paramedService.metrics.runningStatus.value == 'daemonStopped'){
                    paramedService.infoStatus = true;
                }else if(paramedService.metrics.runningStatus.value == 'blocking'){
                    paramedService.stopStatus = false;
                }else{
                    paramedService.infoStatus = false;
                }
            }
            var statusHandle = function (msg, headers, res) {
                //console.log(msg);
                if(!msg.id) return;
                if (!$rootScope.current[msg.id]) $rootScope.current[msg.id] = {};
                var cache=$rootScope.current[msg.id];
                cache.id=msg.id;
                cache.name=msg.name;
                cache.params=angular.fromJson(msg.params);
                cache.url=msg.url;
                cache.ctrlname=msg.ctrlname;
                if (msg && msg.paramedServiceInstances) {
                    var pis=msg.paramedServiceInstances;
                    if(pis){
                        cache._pis=pis;
                        for(key in cache._pis){//外层循环遍历paramedServiceInstances
                            parameHandle(cache._pis[key]);
                        }
                    }
                    for (metric in msg.publicMetrics) {
                        $rootScope.current[msg.id][metric] = msg.publicMetrics[metric].value;
                    }
                }
                if(!$rootScope.$$phase) {
                    $rootScope.$apply();//手动触发angular，去检查更新view
                }


            }

            sc.pullStatus=function () {
                $http.get('/status/pull')
                    .then(function (resp) {
                    console.log(resp);
                })
            }
            sc.handleMonitorObj=statusHandle;
            sc.handleList=function (response) {
                var resp=response.data.data;
                console.log(resp);
                for ( var single in resp) {
                    //sc.addObjToCache(resp[single])
                    sc.handleMonitorObj(resp[single]);
                }
                self.objs = response.data.data;
            };
            sc.addObjToCache=function(obj){
                $rootScope.current[obj.id]=
                {
                    id:obj.id,
                    name:obj.name,
                    url:obj.url,
                    ctrlname:obj.ctrlname,
                    params:angular.fromJson(obj.params)
                };
            };
            sc.initConnect = function () {
                console.log('connecting to push service:')
                $stomp
                    .connect(angular.___context_name___+'/status-notice', {}, function (err) {
                        console.log(err);
                    })
                    // frame = CONNECTED headers
                    .then(function (frame) {
                        console.log(frame);
                        $stomp.subscribe('/status-info-resp', statusHandle, {
                            'headers': 'are awesome'
                        })
                    })
            }
            return sc;


        }
    ])