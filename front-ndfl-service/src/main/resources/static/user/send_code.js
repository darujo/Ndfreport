angular.module('ndflService').controller('sendCodeController', function ($scope, $http, $location,) {

    const constPatchUser = window.location.origin + '/users/system/user/email/code';
    $scope.changEmail = function () {
        if ($scope.load) {
            alert("Подождите обрабатывается предыдущий запрос")
        } else {
            $scope.load = true;
            $http({
                url: constPatchUser,
                method: "get",
                params: {id: $scope.User.id}
            }).then(function (response) {
                $scope.load = false;
                console.log("response :");
                console.log(response);
                alert("Почта будет изменена после подтверждения. Почту отправлено ссылка для подтверждения.")
                $scope.back()


            }, function errorCallback(response) {
                alert(response.data.message);
                $scope.load = false;

            });
        }
    };
    $scope.back = function () {
        $location.path('/');

    }
    console.log("Start");


})