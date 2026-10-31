angular.module('ndflService').controller('requestPassController', function ($scope, $http, $location,) {

    const constPatchUser = window.location.origin + '/users/system/user/password/request';
    $scope.User = {
        nikName: null,
        email: null
    }
    let sendSave = false;
    $scope.requestPass = function () {
        console.log("requestPass");
        console.log($scope.User);
        console.log($scope.User.nikName);
        console.log($scope.User.email);

        if (!sendSave) {
            sendSave = true;
            $http({
                url: constPatchUser,
                method: "get",
                params: {
                    email: $scope.User.email,
                    nikName: $scope.User.nikName
                }
            }).then(function (response) {
                console.log("Save response")
                console.log(response);
                sendSave = false;
                alert("Для восстановления пароля перейдите по ссылка отправленной вам на почту.");
                $scope.back();
            }, function errorCallback(response) {
                sendSave = false;
                console.log(response.data);
                if (!$location.checkAuthorized(response,true)) {

                    alert(response.data);
                }
            });
        }
    }
    $scope.back = function () {
        $location.path('/');

    }
    console.log("Start");


})