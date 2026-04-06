 angular.module('ndflService').controller('userController', function ($scope, $http, $location) {

     const constPatchUser = window.location.origin + '';



    $scope.createUser = function () {
        console.log("createUser");

        $scope.User = {
            id: null,
            nikName: null,
            password: null,
            passwordText: null,
            lastName: null,
            firstName: null,
            patronymic: null,
            passwordChange: false,
            block: false,
            admin: false
        };

        console.log($scope.User);

    };

    $scope.editUser = function (userId) {
        console.log("edit");
        $http.get(constPatchUser + "/users/user/edit/" + userId)
            .then(function (response) {
                $scope.User = response.data;
                console.log($scope.User);

            }, function errorCallback(response) {
                console.log(response)
                if ($location.checkAuthorized(response)) {
                    //     alert(response.data.message);
                }
            });
    };

    let sendSave = false;
    $scope.saveUser = function () {
        console.log("saveUser");
        console.log($scope.User);
        if (!sendSave) {
            sendSave = true;
            $http.post(constPatchUser + "/users/user/edit", $scope.User)
                .then(function (response) {
                    console.log("Save response")
                    console.log(response);
                    sendSave = false;
                    $location.path('/')
                }, function errorCallback(response) {
                    sendSave = false;
                    console.log(response.data);
                    if ($location.checkAuthorized(response)) {

                        alert(response.data.message);
                    }
                });
        }
    }

     $scope.genHash = function (){
         $http({
             url: constPatchUser + "/users/user/password/hash",
             method: "get",
             params: {
                 textPassword: $scope.User ? $scope.User.textPassword : null
             }


         }).then(function (response) {
             console.log("response :");
             console.log(response);
             $scope.User.userPassword= response.data.value;

         }, function errorCallback(response) {

             console.log(response)
             if ($location.checkAuthorized(response)) {
                     alert(response.data.message);
             }

         });


     }

    $scope.cancel = function (){
        $location.path("/");
    }

})