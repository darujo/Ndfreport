angular.module('ndflService').controller('recoveryPassController', function ($scope, $http, $location,) {

    const constPatchUser = window.location.origin + '/users/user/password/recovery';
    $scope.User = {
        passwordNew: null,
        passwordNew2: null
    }
    let paramsStr = new URLSearchParams(location.href.substring(location.href.indexOf("?")));
    let code = paramsStr.get('code');

    if (code === null) {
        console.log("code === null")
        $location.openPath("/")
    }
    let sendSave = false;
    $scope.recoveryPass = function () {
        console.log("recoveryPass");
        console.log($scope.User);
        console.log($scope.User.passwordNew);
        console.log($scope.User.passwordNew2);
        if ($scope.User.passwordNew !== $scope.User.passwordNew2) {
            alert("Новые пароли должны совпадать");
            return;
        }


        if (!sendSave) {
            sendSave = true;
            $http({
                url: constPatchUser,
                method: "get",
                params: {
                    passwordNew: $scope.User.passwordNew,
                    code: code
                }
            }).then(function (response) {
                console.log("Save response")
                console.log(response);
                sendSave = false;
                alert("Пароль успешно изменен.");
                $scope.back();
            }, function errorCallback(response) {
                sendSave = false;
                console.log(response.data);
                if ($location.checkAuthorized(response)) {

                    alert(response.data.message);
                }
            });
        }
    }
    $scope.back = function () {
        $location.openPath('/');

    }


})