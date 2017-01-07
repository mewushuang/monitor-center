angular.module('objInfo').component('objInfo', {

    templateUrl: '/app/obj-info/obj-info.template.html',
    controller: [
        '$http',
        'ObjService',
        'pushService',
        '$routeParams',
        '$scope',
        '$route',
        'Upload',
        function ($http, ObjService, pushService, $routeParams, $scope, $route, Upload) {
            var self = this;

            self.objId = $routeParams.objId;
            self.restart=ObjService.restart;

            self.download = ObjService.download;

            ObjService.logFiles(self.objId, function (response) {
                console.log(response);
                $scope.fileList = response.data.data;
            });

            $scope.parentPath = 'ALL';//页面打开时的默认值
            $scope.$watch('parentPath', function () {//parentPath发生变化时清除验证状态
                $scope.parentStatus=[];
            });
            $scope.parentStatus=[];//用于表单验证，如果上传文件必须选定目录，是ALL验证失败
            //远端配置的可访问目录，如果是ALl，过滤器不进行筛选，否则筛选出对应目录下的文件
            ObjService.parentPath(self.objId, function (resp) {
                self.parents = resp.data.data;
                self.parents.push($scope.parentPath);
            })

            self.lineNum = 20;//获取后面的行数的大小
            self.getNextLines = function (fileName) {
                ObjService.getNextLines(fileName, self.lineNum, function (response) {
                    self.logLines = response.data.data;
                });
            }

            self.deleteLog = function (fileName) {
                ObjService.deleteLog(fileName, function (response) {
                    if(response&&response.data&&response.data.code==200) {
                        for (var i = 0; i < $scope.fileList.length; i++) {
                            if ($scope.fileList[i] == fileName) {
                                $scope.fileList.splice(i, 1);
                            }
                        }
                        //$scope.$apply();
                    }else console.log(response)
                });

            }


            self.searchByPattern = function () {
                ObjService.searchByPattern(self.logFilterPattern, function (response) {
                    self.getNextLines();
                })
            }

            self.searchByPercent = function () {
                ObjService.searchByPercent(self.logFilterPercent, function (response) {
                    self.getNextLines();
                })
            }

            self.resetLogOffset = function () {
                ObjService.resetLogOffset();
            }

            self.clickOnFile = function (fileName) {
                self.fileName = fileName;
                //controller每次使用时被初始化，该方法使用ObjService缓存的objId,
                self.getNextLines(fileName);
            }


            self.upload = function (file) {
                Upload.upload({
                    url: '/file/upload',
                    fields: {'objId': self.objId, 'parent': $scope.parentPath}, // additional data to send
                    file: file
                }).progress(function (evt) {
                    //var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    //console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                }).success(function (data, status, headers, config) {
                    if (data.code == 200)
                        noticUploadStatus(config.file, "success");
                    else
                        noticUploadStatus(config.file, data.msg);
                    //console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
                }).error(function (resp) {
                    noticUploadStatus(file, "failed");
                    console.log(resp)
                });
            }

            function noticUploadStatus(file, status) {
                var files = $scope.files;
                if (files && files.length) {
                    for (var i = 0; i < files.length; i++) {
                        if (files[i].name = file.name) {
                            files[i].uploadStatus = status;
                        }
                    }
                }
            }

            $scope.log = '';

            $scope.uploadFile = function (files) {
                if(!$scope.parentPath||$scope.parentPath=='ALL'){
                    $scope.parentStatus.push('has-error');
                    return;
                }
                if (files && files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        if (!file.$error) {
                            self.upload(file);
                        }
                    }
                }
            };


            self.__showInfo = function (objId, args) {


                self.labels = ["January", "February", "March", "April", "May", "June", "July"];
                self.series = ['Series A', 'Series B'];
                self.data = [
                    [65, 59, 80, 81, 56, 55, 40],
                    [28, 48, 40, 19, 86, 27, 90]
                ];
                self.onClick = function (points, evt) {
                    console.log(points, evt);
                };
                self.datasetOverride = [{yAxisID: 'y-axis-1'}, {yAxisID: 'y-axis-2'}];
                self.options = {
                    scales: {
                        yAxes: [
                            {
                                id: 'y-axis-1',
                                type: 'linear',
                                display: true,
                                position: 'left'
                            },
                            {
                                id: 'y-axis-2',
                                type: 'linear',
                                display: true,
                                position: 'right'
                            }
                        ]
                    }
                };
                self.showObjInfo = true;

            }
        }]
})