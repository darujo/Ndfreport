angular.module('ndflService').controller('recoveryPasswordConfirmController', function ($scope, $http, $location,) {

    const constPatchUser = window.location.origin + '/users/system/user/password/restore';
    $scope.restPass = function () {
        if ($scope.load) {
            alert("Подождите обрабатывается предыдущий запрос")
        } else {
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
                    $location.setToken(nikName, response.data.token);
                    $location.openPath('/recovery_pass', {code: code});
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

    $scope.restPass();
})