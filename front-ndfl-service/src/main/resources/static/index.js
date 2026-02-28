angular.module('ndflService').controller('indexController', function ($rootScope, $scope, $http, $location, $localStorage) {
    const constPatchAuth = window.location.origin;

    $scope.loadFilter = null;
    $scope.tryToAuth = function () {
        $http.post(constPatchAuth + '/auth', $scope.user)
            .then(function successCallback(response) {
                if (response.data.token) {
                    $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                    $localStorage.authUser = {username: $scope.user.username, token: response.data.token};
                    // init();

                    $scope.user = {
                        username: null,
                        password: null
                    };
                    // $location.path('/').search({});
                    $location.path(myHash).search(myFilter);
                    document.getElementById("DetailPrim").open = true;
                }
            }, function errorCallback(response) {
                console.log(response);
                alert("Не удалось авторизоваться")
            });
    };
    $location.backPage = function () {
        $scope.getUser();
        if (myHash === "userPassword".toLowerCase()) {
            $location.path("").search({});
        } else {
            $location.path(myHash).search(myFilter);
        }
    }

    $scope.getUser = function () {
        console.log($localStorage.authUser);
        if (typeof $localStorage.authUser !== "undefined") {
            let nikName = $localStorage.authUser.username;
            console.log("getUser")
            // document.getElementById("UserName").value = nikName;

            $http.get(constPatchAuth + '/users/user?nikName=' + nikName)
                .then(function successCallback(response) {
                    console.log(response)
                    $scope.UserLogin = response.data;
                    $location.UserLogin = $scope.UserLogin;
                    if ($scope.UserLogin.passwordChange) {
                        $location.path('/userPassword'.toLowerCase()).search({});

                    }


                    // document.getElementById("UserName").value = response.data.lastName + " " + response.data.firstName + " " + response.data.patronymic;
                }, function errorCallback(response) {
                    console.log(response);
                });
        }
    }
    $scope.tryToLogout = function () {
        $scope.clearUser();
        $scope.user = {
            username: null,
            password: null
        };
        // if ($scope.user.username) {
        //     $scope.user.username = null;
        // }
        // if ($scope.user.password) {
        //     $scope.user.password = null;
        // }
        $location.path('/').search({});
    };

    $scope.clearUser = function () {
        delete $localStorage.authUser;
        $http.defaults.headers.common.Authorization = '';
    };

    $scope.isUserLoggedIn = function () {
        if ($localStorage.authUser) {
            return true;
        } else {
            $location.path('/').search({});
            return false;
        }
    };

    $scope.isUserLoggedInAndPasOk = function () {
        if (typeof $scope.UserLogin === "undefined") {
            return $scope.isUserLoggedIn();
        } else {
            return !$scope.UserLogin.passwordChange && $scope.isUserLoggedIn();
        }
    };

    $scope.addTelegram = function () {
        console.log("getUser")
        // document.getElementById("UserName").value = nikName;

        $http.get(constPatchAuth + '/users/user/telegram/get')
            .then(function successCallback(response) {
                console.log(response)
                $scope.CodeTelegram = response.data;


                // document.getElementById("UserName").value = response.data.lastName + " " + response.data.firstName + " " + response.data.patronymic;
            }, function errorCallback(response) {
                console.log(response);
            });
    };

    $scope.deleteTelegram = function () {
        console.log("getUser")
        // document.getElementById("UserName").value = nikName;

        $http.get(constPatchAuth + '/users/user/telegram/delete/type')
            .then(function successCallback(response) {
                console.log(response)
                $scope.getUser();
                $scope.CodeTelegram = null;


                // document.getElementById("UserName").value = response.data.lastName + " " + response.data.firstName + " " + response.data.patronymic;
            }, function errorCallback(response) {
                console.log(response);
            });
    };

    $location.checkAuthorized = function (response) {
        console.log("response.status");
        console.log(response.status);
        if (parseInt(response.status) === 500) {
            alert("Произошла ошибка. Обратитесь к администратору. " + response.data.message);
            return false;
        } else if (parseInt(response.status) === 403) {
            alert("Нет прав: " + response.data);
            return false;
        } else if (parseInt(response.status) === 401) {
            checkToken("Вы не авторизованы " + response.data);
            $scope.tryToLogout();
            return false;
        }
        return true;

    }
    
    let myHash;
    let myFilter = {};
    $location.parserFilter = function (filter) {
        console.log("parserFilter");
        console.log(location);
        console.log(location.href);
        let strPars;
        console.log(location.href.lastIndexOf("#!"))
        if (location.href.lastIndexOf("#!") > location.href.indexOf("?")) {
            strPars = location.href.substring(location.href.lastIndexOf("#!"));
            console.log(strPars)
            window.open(location.origin + "/" + strPars, "_self");
            return;
        } else {
            strPars = location.href;
        }
        if (strPars.indexOf("?") !== -1) {
            console.log(strPars)
            let paramsStr = new URLSearchParams(strPars.substring(strPars.lastIndexOf("?")));
            for (let [key, value] of paramsStr.entries()) {
                if (key.toLowerCase().indexOf("stage") !== -1
                    || key.toLowerCase().indexOf("size") !== -1
                    || key.toLowerCase().indexOf("sap") !== -1
                    || key.indexOf("period") !== -1
                    || key.toLowerCase().indexOf("kid") !== -1
                    || key.toLowerCase().indexOf("type") !== -1
                ) {
                    console.log("int");
                    console.log(key);
                    console.log(value);
                    filter[key] = parseInt(value);
                } else if (key.toLowerCase().indexOf("date") !== -1) {
                    console.log(key);

                    console.log(paramsStr.get(key))
                    console.log(new Date(paramsStr.get(key)));

                    filter[key] = new Date(paramsStr.get(key));
                } else if (key.indexOf("weekSplit") !== -1
                    || key.indexOf("workTask") !== -1
                    || key.indexOf("workTime") !== -1
                    || key.indexOf("workPercent") !== -1
                    || key.indexOf("addTotal") !== -1
                    || key.indexOf("ziSplit") !== -1
                    || key.indexOf("hideNotTime") !== -1
                    || key.indexOf("avail") !== -1

                ) {
                    console.log(key);

                    console.log(paramsStr.get(key))
                    console.log(value)
                    filter[key] = paramsStr.get(key) !== "false";
                } else if (key.indexOf("listId") !== -1) {
                    console.log("listId");
                    console.log(typeof filter[key]);
                    console.log(paramsStr.get(key));
                    console.log(paramsStr.getAll(key));

                    if (typeof filter[key] === "undefined") {
                        filter[key] = [];
                    }
                    filter[key].push(parseInt(value));
                    console.log(filter[key]);
                } else {
                    console.log(key);
                    paramsStr.get(key);
                    console.log(value);
                    filter[key] = paramsStr.get(key);
                }

            }
            myFilter = filter;
            myHash = location.hash.substring(location.hash.indexOf("/") + 1, location.hash.indexOf("?"));
        } else {
            myHash = location.hash.substring(location.hash.indexOf("/") + 1);
            myFilter = {};
        }
        console.log("myHash")
        console.log(myHash);
        console.log(filter);
        return filter;
    }
    
    let checkToken = function (message) {
        if ($localStorage.authUser) {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.authUser.token;
            try {
                let jwt = $localStorage.authUser.token;
                let payLoad = JSON.parse(atob(jwt.split(".")[1]));
                let currTime = parseInt((new Date().getTime() / 1000).toString());
                if (currTime > payLoad.exp) {
                    alert("Токен просрочен авторизуйтесь заново.");
                    delete $localStorage.authUser;
                    $http.defaults.headers.common.Authorization = '';
                } else {
                    alert(message);
                }
            } catch (e) {

            }
        }
    }
    console.log("loan index.js end");
})