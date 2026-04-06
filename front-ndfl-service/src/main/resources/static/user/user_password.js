
angular.module('ndflService').controller('userChangeController', function ($scope, $http, $location) {
    const constPatchUser = window.location.origin + '/users';
    $scope.User = { passwordNew:    null,
                    passwordNew2:   null,
                    passwordOld:    null}
    let sendSave = false;
    $scope.saveUser = function () {
        console.log("saveUser");
        console.log($scope.User);
        console.log($scope.User.passwordNew);
        console.log($scope.User.passwordNew2);
        if($scope.User.passwordNew !== $scope.User.passwordNew2){
            alert("Новые пароли должны совпадать");
            return;
        }
        if (!sendSave) {
            sendSave = true;
            $http.post(constPatchUser + "/user/password/change", $scope.User)
                .then(function (response) {
                    console.log("Save response")
                    console.log(response);
                    sendSave = false;
                    $scope.backButton();
                }, function errorCallback(response) {
                    sendSave = false;
                    console.log(response.data);
                    if ($location.checkAuthorized(response)) {

                        alert(response.data.message);
                    }
                });
        }
    }
    $scope.backButton = function () {
        $location.backPage();
    }
})