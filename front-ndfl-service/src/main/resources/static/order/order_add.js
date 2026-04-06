let updateApp = angular.module('ndflService', []);

// DIRECTIVE - FILE MODEL
updateApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            let model = $parse(attrs.fileModel);
            let modelSetter = model.assign;
            console.log(element[0])
            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

updateApp.controller('orderAddController', function ($scope, $http, $location) {
        const constPatchOrder = window.location.origin + '/order-service/order';
        $scope.FormFile = {
            // desc: "",
            files: []
            // ,
            // dateUpdate: new Date(),
            // type: "null"
        }
        $scope.InfoTypes = null;
        $scope.Message = {};
        $scope.sendMessageForAll = false;
        // $scope.onFileSelected = function(event) {
        //     console.log(event)
        //     this.fileToUpload = event.item(0);
        // }

        $scope.sendMessage = function () {
            const formData = new FormData();
            // formData.append('file', $scope.uploadedFile);
            // console.log($scope.uploadedFile)
            console.log(formData);
            // formData.append("description", $scope.FormFile.desc);
            for (let i = 0; i < $scope.FormFile.files.length; i++) {
                formData.append("file", $scope.FormFile.files[i]);
            }
            console.log("SendMessage");
            if (!$scope.sendMessageForAll) {
                $scope.sendMessageForAll = true;

                $http.post(constPatchOrder, formData,
                    {
                        // url: constPatchUpdate,
                        // method: "post",
                        // params: {
                            // dateUpdate: $scope.FormFile.dateUpdate,
                            // type: $scope.FormFile.type
                        // },
                        // data: formData,
                        // reportProgress: true, // Без observe: 'events' не работает
                        // observe: 'events', // без reportProgress: true только HttpEventType.Sent и HttpEventType.Response
                        transformRequest: angular.identity,
                        // блокирут преобразование ответа в объект, а мы этого не хотим
                        // transformResponse: angular.identity,
                        headers: {
                            'Content-Type': undefined
                        },
                        uploadEventHandlers: {
                            progress: function (event) {
                                if (event.lengthComputable) {
                                    console.log(event);
                                    let progressPercentage = Math.round((event.loaded / event.total) * 100);
                                    $scope.uploadProgress = progressPercentage;
                                    console.log('Upload Progress: ' + progressPercentage + '%');
                                }
                            }
                        }

                        // , formData, config
                    }
                )
                    .then(function (response) {
                        console.log("Send Update")
                        console.log(response);
                        $scope.sendMessageForAll = false;
                        alert("Файлы загружены оплатите заказа и после получите отчет");
                    }, function errorCallback(response) {
                        $scope.sendMessageForAll = false;
                        console.log(response);

                        console.log(response.data.message)
                        if ($location.checkAuthorized(response)) {
                            console.log(1)
                            if (response.data.message === undefined) {
                                alert(response.data);
                            } else {
                                alert(response.data.message);
                            }
                        } else {
                            console.log(2)
                            alert(response.data);
                        }
                    });
            } else {
                alert("Подождите отправляется предыдущее обновление")
            }

        }
        let getService = function () {
            console.log("edit");
            $http.get(constPatchOrder + "/services")
                .then(function (response) {
                    $scope.UpdateTypes = response.data;
                    console.log($scope.UpdateTypes);


                }, function errorCallback(response) {
                    console.log(response)
                    if ($location.checkAuthorized(response)) {
                        //     alert(response.data.message);
                    }
                });
        };
        getService();
        console.log("$scope.FormFile.type", $scope.FormFile.type, $scope.FormFile.type === "null");
        $scope.backUser = function () {
            $location.path('/');

        }
        console.log("Start");

        // $scope.loadInfoType();
        $scope.getProcess = function () {
            return {width: $scope.uploadProgress + "%"}
        }
    }
)