angular.module('ndflService').controller('emailConfirmController', function ($scope, $http, $location,) {

    const constPatchUser = window.location.origin + '/users/system/user/email/confirm';
    $scope.checkCode = function () {
        if ($scope.load) {
            alert("Подождите обрабатывается предыдущий запрос")
        } else {
            $scope.load = true;
            $scope.User = null;
            console.log("запрос данных");
            console.log(window.location);
            let paramsStr = new URLSearchParams(location.href.substring(location.href.indexOf("?")));
            let code = paramsStr.get('code');
            let nikName = paramsStr.get('nikName');
            console.log(code)

            $http({
                url: constPatchUser,
                method: "get",
                params: {code: code, nikName: nikName}
            }).then(function (response) {
                $scope.load = false;
                console.log("response :");
                console.log(response);
                if (response.data) {
                    alert("Почта подтверждена")
                    $scope.back()
                } else {
                    alert("Не верный код.")
                }

            }, function errorCallback(response) {
                alert(response.data.message);

            });
        }
    };
    $scope.back = function () {
        $location.path('/');

    }
    console.log("Start");

    $scope.checkCode();
})